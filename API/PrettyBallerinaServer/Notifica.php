<?php

class Notifica {

    private $ServerKey = "AAAA-Z12YIc:APA91bE8NielD8lKIcfY2jnLdkujkPKcpykNjY1pUvTr37OtfO-xtZdRo3Nf6LrO3SRapt2qiIjh6Ok16Q96qNS7aihIqCdvseOnm1frGZCkDx5V1Dw2QH1ngW_Zi1oT_1XbVHgbYn_D";
   // private $registrationIds = "ffl-uwZoNqQ:APA91bHUB8Z2QuVsR6Jxu7MjvQYMPRWr7Sdxy8xC-r1l-vIUSDzZPvnKokpRLfdNJecHeIec0TXLmC2FNc03eHmDqohTl9kblNRo5L4eeU_BqJMody4yn_RSCTEaB0FCc7Hy_OqclyaO";
    private $title = "Pretty Ballerina";
    //private $body = "Nuove Audizioni";

    //private $body = "Nuove Audizioni";
    private $body = "";

    public function __construct( $body ) {

        $this->body = $body;

        $msg = ['body' => $this->body,
            'title' => $this->title,
            'vibrate' => 1,
            'sound' => 1
        ];

       // $fields = ['to' => $this->registrationIds,
        $fields = ['to' => "/topics/pb",
            'notification' => $msg
        ];

        $headers = ['Authorization: key=' . $this->ServerKey,
            'Content-Type: application/json'
        ];

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
        $result = curl_exec($ch);
        curl_close($ch);
        print_r($result);
    }
    

}

   // $a = new Notifica();
