using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;

namespace LightSwitch
{
    class SplashscreenActivity
    {
        private static int DELAY = 1000;

        protected void onCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.activity_splashscreen);

            // Jump to SensorsActivity after DELAY milliseconds 
            new Handler().postDelayed(new Runnable() {
                public void run()
                {
                    //final Intent intent = new Intent(SplashscreenActivity.this, FeaturesActivity.class);

                    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Intent intent = new Intent(SplashscreenActivity.this, LightListActivity.class);
                        startActivity(intent);

                        finish();
                }
            }, DELAY);
	    }
    
        public void onBackPressed()
        {
            // do nothing. Protect from exiting the application when splash screen is shown
        }
    }
}