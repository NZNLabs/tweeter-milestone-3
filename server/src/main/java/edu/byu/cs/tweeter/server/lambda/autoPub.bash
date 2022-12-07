#!/bin/bash
arr=(
        "getFollowing"
        "getFollowers"
        "login"
        "getFollowingCount"
        "getFollowerCount"
        "getFeed"
        "getStory"
        "getFollow"
        "getUnfollow"
        "getIsFollower"
        "postStatus"
        "logout"
        "register"
        "getUser"
    )
for FUNCTION_NAME in "${arr[@]}"
do
  aws lambda update-function-code --function-name $FUNCTION_NAME --zip-file fileb:///Users/airbourne25/AndroidStudioProjects/tweeter-milestone-3/server/build/libs/server-all.jar --region us-west-2 &
done

#!/bin/bash
arr=(
        "getFeed"
    )
for FUNCTION_NAME in "${arr[@]}"
do
  aws lambda update-function-code --function-name $FUNCTION_NAME --zip-file fileb:///Users/airbourne25/AndroidStudioProjects/tweeter-milestone-3/server/build/libs/server-all.jar --region us-west-2 &
done