package com.sammie.barbershopclientmodel;

import com.apps.norris.paywithslydepay.core.SlydepayPayment;

import static com.sammie.barbershopclientmodel.Common.Common.EMAIL_OR_MOBILE_NUMBER;
import static com.sammie.barbershopclientmodel.Common.Common.MERCHANT_KEY;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {

//        if (MissingSplitsManagerFactory.create(this)
//                .disableAppIfMissingRequiredSplits()) {
//            return;
//        }

        super.onCreate();
        new SlydepayPayment(this).initCredentials(EMAIL_OR_MOBILE_NUMBER, MERCHANT_KEY);

//        SweetAlertDialog.DARK_STYLE = true;
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
