# aws-lambda-java
AWSのLambdaのコードをJavaで作っています。
こちらのLambda Java関数は使用するAWSアカウントにてCloudTrailを設定していることが前提です。

# IAM Role
Lambda Javaコードを動かすにはIAM Role for AWS LambdaでEC2とCloudTrailの権限が必要です。
以下、ポリシー記載例です。

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowCloudTrailAccess",
      "Action": "cloudtrail:*",
      "Effect": "Allow",
      "Resource": "*"
    },
    {
      "Sid": "AllowEC2Access",
      "Action": "ec2:*",
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
```