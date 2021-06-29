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
                    "Authorization:key=AAAA8v6b6fU:APA91bE16HsS-W6V00AljR3Jd1jY0AGtxPK0izEeb9BNgkTso2vubB-BTR6GgaOvRTK_D6was6Ub4ygRIu4yMI8F9QzQpmZFGREHW0Qy5YJ9tTDv6cDHJ144LHY9dJZkBgsteKaxcTpS"
            }
    )
    @POST("fcm/send")
    Call<MyResposnse> sendNotification(@Body Sender body);
}
