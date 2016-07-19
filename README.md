#IM版接入说明
_______


##0. 注册表情mm Activity
在AndroidManifest.xml文件中，添加表情mm`Activity`和权限的声明。

```
<!-- 读写权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" >
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" >
<!-- 网络权限 -->
<uses-permission android:name="android.permission.INTERNET" >

<activity
    android:name="com.melink.bqmmsdk.ui.store.EmojiPackageList"
    android:screenOrientation="portrait"
    android:theme="@style/horizontal_slide" >
</activity>
    
<activity
    android:name="com.melink.bqmmsdk.ui.store.EmojiPackageDetail"
    android:screenOrientation="portrait"
    android:theme="@style/horizontal_slide" >
</activity>
    
<activity
    android:name="com.melink.bqmmsdk.ui.store.EmojiPackageDown"
    android:screenOrientation="portrait"
    android:theme="@style/horizontal_slide" >
</activity>
    
<activity
    android:name="com.melink.bqmmsdk.ui.store.EmojiPackageMyCollection"
    android:screenOrientation="portrait"
    android:theme="@style/horizontal_slide" >
</activity>
    
<activity
    android:name="com.melink.bqmmsdk.ui.store.EmojiPackageSetting"
    android:screenOrientation="portrait"
    android:theme="@style/horizontal_slide" >
</activity>
    
 <activity
    android:name="com.melink.bqmmsdk.ui.store.EmojiDetail"
    android:screenOrientation="portrait"
    android:theme="@style/horizontal_slide" >
</activity>
    
<activity
    android:name="com.melink.bqmmsdk.ui.store.ServiceDeclaration"
    android:screenOrientation="portrait"
    android:theme="@style/horizontal_slide" >
</activity>
<activity
    android:name="com.melink.bqmmsdk.ui.store.EmojiPackageSort"
    android:screenOrientation="portrait"
    android:theme="@style/bqmm_horizontal_slide" >
</activity>

```


##1. 获得SDK实例对象
BQMM实例对象使用单例实现，通过静态方法`BQMM.getInstance()`获得。

##2. 初始化SDK
初始化SDK时，需要设置后台添加的应用AppId和AppSecret。

```java
BQMM bqmmsdk = BQMM.getInstance();
bqmmsdk.initConfig(context, YOUR_APP_ID, YOUR_APP_SECRET);
```

***说明：APP_ID和APP_SECRET信息可在[注册页面](http://open.biaoqingmm.com/open/register/index.html)获取***

##3. 添加BQMM键盘
SDK提供继承自`RelativeLayout`的`BQMMKeyboard`类实现表情键盘。
使用时在布局文件中加入控件即可。

```java
<!-- 集成BQMM表情键盘 -->
<com.melink.bqmmsdk.ui.keyboard.BQMMKeyboard
        android:id="@+id/bqmm_keyboard"
        android:layout_width="match_parent"
        android:layout_height="250dp"
         />
```
添加发送按钮`BQMMSendButton`（继承原生`Button`）。

```java
<com.melink.bqmmsdk.widget.BQMMSendButton
    android:id="@+id/btn_send"
    android:layout_width="wrap_content"
    android:layout_height="32dp"
    android:layout_marginRight="4dp"
    android:background="@drawable/chat_send_btn_selector"
    android:onClick="onClick"
    android:text="@string/button_send"
    />
```
添加消息编辑控件`BQMMEditView`（继承原生`EditText`）。

```java
<com.melink.bqmmsdk.widget.BQMMEditView
    android:id="@+id/et_sendmessage"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginRight="35dip"
    android:background="@null"
    android:maxLines="3"
    android:minHeight="40dp"
    android:onClick="editClick"
    />
```

在相应`Activity`中初始化，并添加至SDK实例。

```java
bqmmsdk = BQMM.getInstance();
bqmmKeyboard = (BQMMKeyboard) findViewById(R.id.bqmm_keyboard);
bqmmSendButton = (BQMMSendButton) findViewById(R.id.btn_send);
bqmmEditText = (BQMMEditView) findViewById(R.id.et_sendmessage);

//初始化表情MM键盘，需要传入关联的EditView,SendBtn
bqmmsdk.setEditView(bqmmEditText);
bqmmsdk.setKeyboard(bqmmKeyboard);
bqmmsdk.setSendButton(bqmmSendButton);
bqmmsdk.load();
```

##4. 实现表情键盘和软键盘切换按钮

可以直接调用`BQMMKeyboard.showKeyboard()`和`BQMMKeyboard. hideKeyboard()`方法实现表情键盘的显示和隐藏。
`BQMMKeyboard.isKeyboardVisible()`可以检查键盘是否开启。

##5. 发送表情消息

SDK中的`IBqmmSendMessageListener`是消息发送的回调，以下是Demo中的示例代码，具体发送方法可以根据开发者自己的业务需求实现。

```java
bqmmsdk.setBqmmSendMsgListener(new IBqmmSendMessageListener() {
   /**
   * 点击发送单个大表情的回调
   * <param>face</param> 大表情对象
   */
   @Override
   public void onSendEmoji(Emoji face) {
      //表情消息发送
      sendFaceText(face.getEmoCode() ,FACETYPE); 
   }

   /**
   * 从BQMMEditView中发送图文混排表情的回调方法
   * <param>emojis</param> emoji和文本消息的列表
   * <param>isMixedMessage</param> 是否回调消息
   */
   @Override
   public void onSendMixedMessage(List<Object> emojis, bool isMixedMessage) {
      
      //将emojis转换为可发送的消息字符串
      String msgString = getMixedMessageString(emojis);

      if(isMixedMessage){
         //图文混排表情消息发送
         sendMixedText(msgString, EMOJITYPE);
      }else{
         //纯文本消息
         sendText(msgString);
      }
   }
});
```

##6. 消息显示


>SDK1.6版本移除`BQMMMessageView`控件，`BQMMMessageText`做为默认支持消息展示控件

`BQMMMessageText`继承与原生`TextView`，兼容大表情展示，混排gif表情展示

  在`Adapter`中调用`showMessage`方法显示表情消息：

  ```
  /**
   * 
   * @Title: showMessage
   * @Description: 展示表情消息
   * @param msgTextString
   *            消息的显示格式
   * @param msgType
   *            消息的类型
   *           EMOJITYPE 混排表情消息，FACETYPE 单表情消息 
   * @param msgData
   *            消息的JSONArray格式
   * @return: void
   */
  public void showMessage(String msgId, String msgTextString, String msgType,
      JSONArray msgData) {
    }
  ```

  设置表情的显示大小：

  ```
   /**
    * 设置控件中展示大表情的尺寸大小
    * 
    * @param size
    */
   public void setBigEmojiShowSize(int size) {
   }

   /**
    * 设置控件中展示小表情的尺寸大小
    * 
    * @param size
    */
   public void setSmallEmojiShowSize(int size) {
   }
   ```


##7. 解析表情消息

表情mm SDK提供默认的表情消息格式。举个栗子：

```
你好，😄。我是Annie😝😝
```

编辑上面的消息，点击发送按钮之后，在`IBqmmSendMessageListener.onSendMixedMessage`回调方法中，会得到内容为内容`["你好，", Emoji, "。我是Annie", Emoji, Emoji]`的列表。
`BQMMMessageHelper`提供将以上列表直接转换为表情消息的方法。

```java
bqmmsdk.setBqmmSendMsgListener(new IBqmmSendMessageListener() {
	@Override
	public void onSendFace(Emoji face) {
	   JSONArray msgCodes = BQMMMessageHelper.getFaceMessageData(face);
	   sendFaceText(BQMMMessageHelper.getFaceMessageString(face),msgCodes,FACETYPE); 
	}
	@Override
	public void onSendMixedMessage(List<Object> emojis, boolean isMixedMessage) {
		String msgString = BQMMMessageHelper.getMixedMessageString(emojis);
		//判断一下是纯文本还是富文本
		if(isMixedMessage){
		    //发送图文混排的表情消息		
		    JSONArray msgCodes = BQMMMessageHelper.getMixedMessageData(emojis);
		    sendFaceText(msgString,msgCodes, EMOJITYPE);
		}else{
		    //发送文字消息
		    sendText(msgString);
		}
	}
});

```

表情mm的默认表情消息结构是一个包含文本消息和`emojiCode`的二维数组返回，继续上面的消息栗子，`BQMMMessageHelper.getMixedMessageData(List<Object> content)`方法将文本和`Emoji`对象的列表转换为：

```
[["你好，", 0], ["haha", 1], ["。我是Annie", 0], ["biyan", 1], ["biyan", 1]]
```

其中：

* `["text", "0"]` 表示文本消息
* `["text", "1"]` 表示小表情消息
* `["text", "2"]` 表示大表情消息

表情类型：

* `EMOJITYPE` 混排表情消息
* `FACETYPE` 单表情消息 


开发者也可以使用自定义的消息格式，在使用`BQMMMessageText`显示表情消息前，转换为上述格式即可。


##8. 下载表情

`emojiCode`是表情mm对表情对象`Emoji`的唯一编码，可以通过`Emoji.getEmoCode()`方法读取。

同时，在消息预览中，可以显示中文的表情含义词`Emoji.getEmoText()`以提高消息预览中的可读性。

`BQMM.java`提供使用`emojiCode`从后台下载大表情和小表情的方法。

* 批量下载大表情

```java
public void fetchBigEmojiByCodeList(Context context,List<String> codelist ,final IFetchEmojisByCodeListCallback callback)
```

* 批量下载小表情

```java
public void fetchSmallEmojiByCodeList(Context context,List<String> codelist,final IFetchEmojisByCodeListCallback callback)
```

参数`List<String> codelist`是一个`emojiCode`的列表。
表情下载成功后，执行`IFetchEmojisByCodeListCallback`的回调方法。开发者需要处理表情下载成功或者失败时的情况。

##9. 显示表情详情预览

`EmojiDetail.java`表情mm提供的单个表情预览页面，用户可以在此查看表情图片并直接下载先关表情包。它是`FrameActivity`的子类。
通常会将下面的调用代码加到表情图片的点击事件中，实现表情点击预览：

```java
Intent it = new Intent(cxt, EmojiDetail.class);
Bundle bundle = new Bundle();
bundle.putSerializable("Emoji_Detail", emoji);
it.putExtras(bundle);
cxt.startActivity(it); 
```

##10. 实现表情联想输入

`BQMMEditView`的`onTextChanged`方法提供了表情联想功能。如果输入已下载大表情的名称关键字，输入框上方会自动出现相关表情，显示5秒，点击发送或无操作后消失。

```
bqmmEditText.addTextChangedListener(new TextWatcher() {
    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {

      //设置联想窗口显示的位置
      BQMM.getInstance().startShortcutPopupWindowByoffset(ChatActivity.this, s.toString(),bqmmSendButton,0,40);
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
    }
    
    @Override
    public void afterTextChanged(Editable s) {
    }
});
```


##11. UI定制
可定制范围：

* 键盘上商店按钮的颜色和背景颜色
* 键盘背景色
* 键盘上发送按钮的字体和背景颜色
* 小表情键盘上的删除图标
* 键盘加载失败的文字提示和按钮样式
* 商店导航栏标题字体
* 商店导航栏背景色
* 商店导航栏返回图标和设置图标
* 下载按钮字体、颜色，以及背景色
* 商店加载失败的文字提示和按钮样式

开发者可以在`BQMM_Lib`项目的`res`文件夹下修改以上配置。

##12. 设置接入方App UserId（可选）

对App内单个用户的表情使用情况，可以在初始化SDK后设置App的UserId，方便跟踪统计。

```java
public void setAppUserID(String appUserID)
```

