package com.android.mb.hd;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * <p>JSON帮助类(基于Gson)</p>
 * <p>可使用@SerializedName为POJO类中的成员变量起别名</p>
 * <p>可使用@Expose排除POJO类中的某一成员变量,但现在还未解决...但不怎么影响使用...只是不怎么</p>
 * <p>在使用列表结构时,请统一定义为List,实例化时使用LinkedList,要不会报类型不对</p>
 */
public class JsonHelper {

	private static Gson sGson = null;
	private static Gson sExposeGson = null;

	/**
	 * 取得gson对象
	 *
	 * @return gson对象
	 */
	public static Gson getGson() {
		if (sGson == null) {
			sGson = new Gson();
		}
		return sGson;
	}

	/**
	 * 取得具有Expose属性的gson对象
	 *
	 * @return 具有Expose属性的gson对象
	 */
	public static Gson getExposeGson() {
		if (sExposeGson == null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.excludeFieldsWithoutExposeAnnotation();
			sExposeGson = gsonBuilder.create();
		}
		return sExposeGson;
	}

	/**
	 * 将对象序列化为JSON字符串
	 *
	 * @param src json对象
	 * @return JSON字符串
	 */
	public static String toJson(Object src) {
		return getGson().toJson(src);
	}

	/**
	 * 根据指定类型将对象序列化为JSON字符串
	 *
	 * @param src       json对象
	 * @param typeOfSrc 对象类型
	 * @return JSON字符串
	 */
	public static String toJson(Object src, Type typeOfSrc) {
		return getGson().toJson(src, typeOfSrc);
	}

	/**
	 * 将对象序列化为JSON字符串，过滤Expose
	 *
	 * @param src json对象
	 * @return JSON字符串
	 */
	public static String toExposeJson(Object src) {
		return getExposeGson().toJson(src);
	}

	/**
	 * 根据指定类型将对象序列化为JSON字符串，过滤Expose
	 *
	 * @param src       json对象
	 * @param typeOfSrc 对象类型
	 * @return JSON字符串
	 */
	public static String toExposeJson(Object src, Type typeOfSrc) {
		return getExposeGson().toJson(src, typeOfSrc);
	}

	/**
	 * 从JSON字符串反序列化为指定类型的对象
	 *
	 * @param json     JSON字符串
	 * @param classOfT 对象类型
	 * @return 实体
	 */
	public static <T> T fromJson(String json, Class<T> classOfT) {
		try {
			return getGson().fromJson(json, classOfT);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从JSON对象反序列化为指定类型的对象
	 *
	 * @param json     JSON字符串
	 * @param classOfT 对象类型
	 * @return 实体
	 */
	public static <T> T fromJson(JSONObject json, Class<T> classOfT) {
		if (json != null) {
			return fromJson(json.toString(), classOfT);
		}
		return null;
	}

	/**
	 * 从JSON字符串反序列化为指定类型的对象
	 *
	 * @param json    JSON字符串
	 * @param typeOfT 对象类型
	 * @return 实体
	 */
	public static <T> T fromJson(String json, Type typeOfT) {
		try {
			return getGson().fromJson(json, typeOfT);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从JSON对象反序列化为指定类型的对象
	 *
	 * @param json    JSON字符串
	 * @param typeOfT 对象类型
	 * @return 实体
	 */
	public static <T> T fromJson(JSONObject json, Type typeOfT) {
		if (json != null) {
			return fromJson(json.toString(), typeOfT);
		}
		return null;
	}

	/**
	 * 从JSON字符串反序列化为指定类型的对象，过滤Expose
	 *
	 * @param json     JSON字符串
	 * @param classOfT 对象类型
	 * @return 实体
	 */
	public static <T> T fromExposeJson(String json, Class<T> classOfT) {
		try {
			return getExposeGson().fromJson(json, classOfT);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从JSON对象反序列化为指定类型的对象，过滤Expose
	 *
	 * @param json     JSON字符串
	 * @param classOfT 对象类型
	 * @return 实体
	 */
	public static <T> T fromExposeJson(JSONObject json, Class<T> classOfT) {
		if (json != null) {
			return fromExposeJson(json.toString(), classOfT);
		}
		return null;
	}

	/**
	 * 从JSON字符串反序列化为指定类型的对象，过滤Expose
	 *
	 * @param json    JSON字符串
	 * @param typeOfT 对象类型
	 * @return 实体
	 */
	public static <T> T fromExposeJson(String json, Type typeOfT) {
		try {
			return getExposeGson().fromJson(json, typeOfT);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从JSON对象反序列化为指定类型的对象，过滤Expose
	 *
	 * @param json    JSON字符串
	 * @param typeOfT 对象类型
	 * @return 实体
	 */
	public static <T> T fromExposeJson(JSONObject json, Type typeOfT) {
		if (json != null) {
			return fromExposeJson(json.toString(), typeOfT);
		}
		return null;
	}
}

