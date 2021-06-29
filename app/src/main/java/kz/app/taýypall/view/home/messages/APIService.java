package kz.app.taýypall.view.home.messages;

import kz.app.taýypall.notification.MyResposnse;
import kz.app.taýypall.notification.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAADlogTS0:APA91bHpiWLwPQBvihrQo2WPt0mIaJqWnctLy6013fOCs-2IjXi4ps72c0bpvKwBF9AIjG2-MdtThAABcKmXXRaLT5jxRMwnlls7Au2Q7Rud0oUTHp9vkh_TviUwNpCFlmlbQMW7fZ_H"
            }
    )
    @POST("fcm/send")
    Call<MyResposnse> sendNotification(@Body Sender body);
}
