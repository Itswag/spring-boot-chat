package cn.srblog.chat.util;

import cn.srblog.chat.common.Result;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class QCloudSmsUtil {

	/**
	 * 请配置腾讯云短信配置
	 */
	private final static int appId = 0;
	private final static String appKey = "*";
	private final static int templateId = 0;
	private final static String smsSign = "*";

	public static Result sendMessage(String phoneNumber, String name, String code){
		try {
			String[] params = { name, code };
			SmsSingleSender singleSender = new SmsSingleSender(appId, appKey);
			SmsSingleSenderResult result = singleSender.sendWithParam("86", phoneNumber, templateId, params, smsSign, "", "");
			log.info(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return Result.createByErrorMessage("发送失败");
		}

		return Result.createBySuccess("发送成功");
	}

}
