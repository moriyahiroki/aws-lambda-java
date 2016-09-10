
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

		//東京リージョンのEC2情報を全て取得
		DescribeInstancesResult describeInstanceResult = ec2.describeInstances();

		//EC2の情報を取得
		List<Reservation> reservations = describeInstanceResult.getReservations();

		String str = "";

		//EC2の情報を順次取得して、タグの確認をする
		for (Reservation reservation  : reservations){
			List<Instance> instances = reservation.getInstances();

			// Ownerのタグキーがある順次確認する
			for (Instance instance : instances){
				int setTagFlag = 0;
				str += instance.getInstanceId();
				List<Tag> tags = instance.getTags();
				int ownerKeyFlag = 0;
				for (Tag tag : tags){
					if (tag.getKey().equals("Owner")){
						ownerKeyFlag = 1;
					}
				}
				// OwnerTagがついていなければつける
				if(ownerKeyFlag == 0){
					AWSCloudTrailClient cloudtrail = new AWSCloudTrailClient();
					cloudtrail.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

					LookupEventsRequest lookupEventsRequest = new LookupEventsRequest();
					Collection<LookupAttribute> lookupAttributes = new ArrayList<LookupAttribute>();
					LookupAttribute lookupAttribute = new LookupAttribute();
					lookupAttribute.setAttributeKey(LookupAttributeKey.EventName);
					lookupAttribute.setAttributeValue("RunInstances");
					lookupAttributes.add(lookupAttribute);
					lookupEventsRequest.setLookupAttributes(lookupAttributes);

					LookupEventsResult lookupEventsResult = cloudtrail.lookupEvents(lookupEventsRequest);
					//LookupEventsResult lookupEventsResult = cloudtrail.lookupEvents();
					List<Event> events = lookupEventsResult.getEvents();
					for (Event event : events){
						for (Resource resource : event.getResources()){
							if(resource.getResourceName().equals(instance.getInstanceId())){
								//str += resource.getResourceName() + " : " + event.getUsername() + "<end>";
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
						str += "(Set owner tag)";
					}else{
						str += "(Can't find owner)";
					}
				}else{
					str += "(OK)";
				}
				str += "  ";
			}
		}

		return new Response(str);
	}

}
