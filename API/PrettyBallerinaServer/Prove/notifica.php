<?php

 $ServerKey = "AAAA-Z12YIc:APA91bE8NielD8lKIcfY2jnLdkujkPKcpykNjY1pUvTr37OtfO-xtZdRo3Nf6LrO3SRapt2qiIjh6Ok16Q96qNS7aihIqCdvseOnm1frGZCkDx5V1Dw2QH1ngW_Zi1oT_1XbVHgbYn_D";
$registrationIds = "d49ikZi8GPE:APA91bEpUhfGnUBXTVicXvxw88ZFAIa0Fs6uSv3Yx9QvPKRooFJ2gjqcPQHlYsnrxTMq2GH40ZQXJx3lRjtnfkPfre4rorhAUTRTxOx6TWFni9LsulFOLQHvv2QrzOeCc83VYTTb607Q";



$body = "Jasmin è di fronte a te! Vuoi provarci?";
$title = "Ragazza bellissima nelle vicinanze";


$msg = array
(
    'body' => $body,
    'title' => $title,
    'vibrate' => 1,
    'sound' => 1,
);

$fields = array
(
//    'to' => $registrationIds,
    'to' => "/topics/pb",
    'notification' => $msg
);

$headers = array
(
    'Authorization: key=' . $ServerKey,
    'Content-Type: application/json'
);


$ch = curl_init();
curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
curl_setopt( $ch,CURLOPT_POST, true );
curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
$result = curl_exec($ch );
curl_close( $ch );
print_r($result);