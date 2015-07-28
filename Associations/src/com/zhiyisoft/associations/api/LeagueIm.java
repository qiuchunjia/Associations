package com.zhiyisoft.associations.api;

import java.util.List;

import com.zhiyisoft.associations.model.base.Model;

/**
 * author：qiuchunjia time：上午11:34:57 接口描述：实现创建社团
 *
 */

public interface LeagueIm {
	public static final String GROUP = "group";
	public static final String CREATEGROUP = "creategroup";
	public static final String GETGROUPCOMMONLIST = "getgroupcommonlist";

	/**
	 * 創建社團
	 * 
	 * @param modelItem
	 *            需要传递的社团model
	 * @return
	 */
	Object createLeague(Model modelItem);

	/**
	 * 获取社团的分类
	 * 
	 * @param mItem
	 * @return
	 */
	List<Model> getGroupCommonList(Model mItem);
}