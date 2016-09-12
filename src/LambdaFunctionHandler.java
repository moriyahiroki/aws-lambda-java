
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.cloudtrail.model.Event;
import com.amazonaws.services.cloudtrail.model.LookupAttribute;
import com.amazonaws.services.cloudtrail.model.LookupAttributeKey;
import com.amazonaws.services.cloudtrail.model.LookupEventsResult;
import com.amazonaws.services.cloudtrail.model.LookupEventsRequest;
import com.amazonaws.services.cloudtrail.AWSCloudTrailClient;
import com.amazonaws.services.cloudtrail.model.Resource;

public class LambdaFunctionHandler implements RequestHandler<Request, Response> {

	@Override
	public Response handleRequest(Request input, Context context) {
		context.getLogger().log("Input: " + input);

		// EC2へのアクセス設定
		AmazonEC2Client ec2 = new AmazonEC2Client();
		ec2.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

		// 東京リージョンのEC2情報を全て取得
		DescribeInstancesResult describeInstanceResult = ec2.describeInstances();

		// EC2の情報を取得
		List<Reservation> reservations = describeInstanceResult.getReservations();
		
		// 結果を返す文字列を初期化
		String str = "";

		// EC2の情報を順次取得して、タグの確認をする
		for (Reservation reservation  : reservations){
			List<Instance> instances = reservation.getInstances();

			// Ownerのタグキーがある順次確認する
			for (Instance instance : instances){
				
				// Ownerタグが設定されているか確認するフラグ
				int setTagFlag = 0;
				str += instance.getInstanceId();
				
				// インスタンスに設定されているタグを全て取得
				List<Tag> tags = instance.getTags();
				int ownerKeyFlag = 0;
				
				// タグの中にOwnerタグが設定されているか確認
				for (Tag tag : tags){
					if (tag.getKey().equals("Owner")){
						ownerKeyFlag = 1;
					}
				}
				// OwnerTagがついていなければつける
				if(ownerKeyFlag == 0){
					// CloudTrailのAPI操作履歴からタグが付いていないインスタンスを立ち上げたIAMユーザを検索する
					AWSCloudTrailClient cloudtrail = new AWSCloudTrailClient();
					cloudtrail.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
					
					// CloudTrailのLookEvent処理で操作APIを検索する
					LookupEventsRequest lookupEventsRequest = new LookupEventsRequest();
					Collection<LookupAttribute> lookupAttributes = new ArrayList<LookupAttribute>();
					LookupAttribute lookupAttribute = new LookupAttribute();
					
					// CloudTrailのEvent検索でEventNameを"RunInstances"で絞る
					lookupAttribute.setAttributeKey(LookupAttributeKey.EventName);
					lookupAttribute.setAttributeValue("RunInstances");
					lookupAttributes.add(lookupAttribute);
					lookupEventsRequest.setLookupAttributes(lookupAttributes);
					
					// Eventの検索を実行
					LookupEventsResult lookupEventsResult = cloudtrail.lookupEvents(lookupEventsRequest);
					List<Event> events = lookupEventsResult.getEvents();
					
					// LookupしたEventを検索してタグが付いていないResourceの操作Eventを検索
					for (Event event : events){
						for (Resource resource : event.getResources()){
							if(resource.getResourceName().equals(instance.getInstanceId())){
								
								// Eventで該当インスタンスのUsernameが特定できたらOwnerタグとしてUsernameをつける
								Tag setTag = new Tag("Owner",event.getUsername());
								List<String> resourceList = new ArrayList<String>();
								List<Tag> tagList = new ArrayList<Tag>();
								resourceList.add(resource.getResourceName());
								tagList.add(setTag);
								ec2.createTags(new CreateTagsRequest(resourceList, tagList));
								setTagFlag = 1;
							}
						}
					}
					
					if (setTagFlag == 1){
						// タグを新たにつけた場合の出力
						str += "(Set owner tag)";
					}else{
						// 該当インスタンスを立ち上げた人が不明だった場合
						str += "(Can't find owner)";
					}
				}else{
					// Ownerタグがすでに付いていた場合
					str += "(OK)";
				}
				str += "  ";
			}
		}

		return new Response(str);
	}

}
