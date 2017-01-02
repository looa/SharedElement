# PrismSE
Prism SharedElement support OS on API 11 and above.

## Using TabView in your Application
If you are building with Gradle, simply add the following line to the dependencies section of your build.gradle file:
````groovy
compile 'com.github.looa:SharedElement:-SNAPSHOT'
````
Add it in your root build.gradle at the end of repositories:
````groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
````
## Sample
Java
````groovy
adapter.setOnItemClickListener(new SimpleAdapter.OnItemClickListener() {
     @Override
     public void onItemClick(View view, int position) {
          Intent intent = new Intent();
          intent.setClass(MainActivity.this, SubActivity.class);
          PrismSE.getInstant().startActivity(view, intent, true);
     }
});
````
and
````groovy
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sub);
    imageView = (ImageView) findViewById(R.id.iv_sub);
    PrismSE.getInstant().initSharedElement(imageView);
}

```
@Override
public void onBackPressed() {
    PrismSE.getInstant().finish(imageView);
    PrismSE.getInstant().overridePendingTransition(0, android.R.anim.fade_out);
}
````