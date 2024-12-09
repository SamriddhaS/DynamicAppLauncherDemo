<h1 align="center">Dynamic App Launcher Icon Demo</h1>

<p align="center">
This project demonstrates how to implement dynamic app launcher icons in Android applications using Jetpack Compose. Popular apps like Swiggy, Zomato, and Twitter use this feature to dynamically change their app icons, enhancing user engagement and branding.
</p>

---

<h2>📌 Features</h2>

<ul>
  <li>Switch between multiple app launcher icons dynamically.</li>
  <li>Uses <code>ActivityAlias</code> to manage app icons.</li>
  <li>Includes a Jetpack Compose-based UI to select and apply app icons seamlessly.</li>
</ul>

---

<h2>🎥 Preview</h2>
<p align="center">
  <img src="/screenshots/demo.gif" align="center" />
</p>

---

<h2>📋 Table of Contents</h2>

<ol>
  <li><a href="#setup-and-requirements">Setup and Requirements</a></li>
  <li><a href="#implementation-highlights">Implementation Highlights</a></li>
  <li><a href="#usage">Usage</a></li>
  <li><a href="#blog-links">Blog Links Reated to this topic</a></li>
  <li><a href="#contributing">Contributing</a></li>
</ol>

---

<h2 id="setup-and-requirements">⚙️ Setup and Requirements</h2>

<h3>Requirements</h3>
<ul>
  <li>Android Studio Arctic Fox or later.</li>
  <li>Minimum SDK version: 21.</li>
  <li>Recommended target SDK version: 31 or higher.</li>
</ul>

<h3>Setup</h3>
<ol>
  <li>Clone the repository:
    <pre><code>git clone https://github.com/SamriddhaS/DynamicAppLauncherDemo.git</code></pre>
  </li>
  <li>Open the project in Android Studio.</li>
  <li>Build and run the project on a physical device or emulator.</li>
</ol>

---

<h2 id="implementation-highlights">✨ Implementation Highlights</h2>

<h3>1. Manifest Configuration</h3>
<p>We define <code>ActivityAlias</code> entries in the <code>AndroidManifest.xml</code> to manage multiple app icons. Each alias is mapped to the main activity but uses a different icon. We can add activity alias for each of the launcher icon that we want to configure.</p>

<pre>
<code>&lt;!-- This is enabled by default --&gt;
&lt;activity-alias
    android:name=".DefaultTheme"
    android:enabled="true"
    android:icon="@mipmap/ic_launcher"
    android:targetActivity=".MainActivity"
    android:exported="true"&gt;
    &lt;intent-filter&gt;
        &lt;action android:name="android.intent.action.MAIN" /&gt;
        &lt;category android:name="android.intent.category.LAUNCHER" /&gt;
    &lt;/intent-filter&gt;
&lt;/activity-alias&gt;

&lt;!-- Icon one: disabled by default --&gt;
&lt;activity-alias
    android:name=".MyIconOne"
    android:enabled="false"
    android:icon="@mipmap/my_launch_icon_one"
    android:targetActivity=".MainActivity"
    android:exported="true"&gt;
    &lt;intent-filter&gt;
        &lt;action android:name="android.intent.action.MAIN" /&gt;
        &lt;category android:name="android.intent.category.LAUNCHER" /&gt;
    &lt;/intent-filter&gt;
&lt;/activity-alias&gt;

&lt;!-- Icon two: disabled by default --&gt;
&lt;activity-alias
    android:name=".MyIconTwo"
    android:enabled="false"
    android:icon="@mipmap/my_launch_icon_two"
    android:targetActivity=".MainActivity"
    android:exported="true"&gt;
    &lt;intent-filter&gt;
        &lt;action android:name="android.intent.action.MAIN" /&gt;
        &lt;category android:name="android.intent.category.LAUNCHER" /&gt;
    &lt;/intent-filter&gt;
&lt;/activity-alias&gt;
</code>
</pre>

<h3>2. Dynamic Icon Switching</h3>
<p>The <code>MainActivity</code> uses the <code>PackageManager</code> API to enable or disable <code>ActivityAlias</code> entries dynamically.</p>

<pre>
<code>packageManager.setComponentEnabledSetting(
    ComponentName(
        this,
        "$packageName${selectedIcon.themeName}"
    ),
    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
    PackageManager.DONT_KILL_APP
)

packageManager.setComponentEnabledSetting(
    ComponentName(
        this,
        "$packageName${currentIcon}"
    ),
    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
    PackageManager.DONT_KILL_APP
)
</code>
</pre>

<h3>3. Shared Preferences</h3>
<p>We store the currently active icon in <code>SharedPreferences</code> to persist user preferences.</p>

<pre>
<code>sharedPref.edit {
    putString(CURRENT_ICON_KEY, selectedIcon.themeName)
    apply()
}
</code>
</pre>

<h3>4. Jetpack Compose UI</h3>
<p>A Compose-based UI allows users to select and preview the app icons dynamically.</p>

<pre>
<code>@Composable
fun ImageSelector(
    onImageClick: (AppLauncherIcons) -> Unit,
    modifier: Modifier
) {
    val imageList = listOf(
        AppLauncherIcons.Default_Theme,
        AppLauncherIcons.Theme_One,
        AppLauncherIcons.Theme_Two,
        AppLauncherIcons.Theme_Three,
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(imageList.size) { index ->
            val imageRes = imageList[index]
            GlideImage(
                model = imageRes.imageRes,
                contentDescription = "Image $index",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clickable { onImageClick(imageRes) }
                    .padding(4.dp)
            )
        }
    }
}
</code>
</pre>

---

<h2 id="usage">🚀 Usage</h2>

<ol>
  <li>Launch the app.</li>
  <li>Browse through the available launcher icons using the provided UI.</li>
  <li>Select an icon to apply it as the app launcher icon.</li>
  <li>Confirm the selection in the modal bottom sheet.</li>
</ol>

---

<h2 id="blog-links">📝 Blog Links</h2>

<p>Here are some helpful blogs that inspired and guided the implementation of this feature:</p>

<ul>
  <li><a href="https://oguzhanaslann.medium.com/dynamic-app-icon-in-android-a61f8570ab9f" target="_blank">Dynamic App Icon in Android by Oğuzhan Aslan</a></li>
  <li><a href="https://blog.famapp.in/blog/change-app-icon-dynamically-in-android/" target="_blank">Change App Icon Dynamically in Android by FamApp Blog</a></li>
</ul>

---


<h2 id="contributing">🤝 Contributing</h2>

<p>Contributions are welcome! Please fork the repository, make your changes, and submit a pull request.</p>

---

<p align="center"><strong>Enjoy experimenting with dynamic app icons! 🚀</strong></p>
