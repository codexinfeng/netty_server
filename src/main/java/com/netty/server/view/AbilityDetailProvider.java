package com.netty.server.view;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.netty.server.core.ReflectionUtils;
import com.netty.server.netty.MessageRecvExecutor;

public class AbilityDetailProvider implements AbilityDetail {
	private final static String STYLE = "<style type=\"text/css\">\n"
			+ "table.gridtable {\n"
			+ "    font-family: verdana,arial,sans-serif;\n"
			+ "    font-size:11px;\n" + "    color:#333333;\n"
			+ "    border-width: 1px;\n" + "    border-color: #666666;\n"
			+ "    border-collapse: collapse;\n" + "}\n"
			+ "table.gridtable th {\n" + "    border-width: 1px;\n"
			+ "    padding: 8px;\n" + "    border-style: solid;\n"
			+ "    border-color: #666666;\n"
			+ "    background-color: #dedede;\n" + "}\n"
			+ "table.gridtable td {\n" + "    border-width: 1px;\n"
			+ "    padding: 8px;\n" + "    border-style: solid;\n"
			+ "    border-color: #666666;\n"
			+ "    background-color: #ffffff;\n" + "}\n" + "</style>";

	private final static String HEADER = "<table class=\"gridtable\">\n"
			+ "<tr>\n" + "  <th>NettyRPC Ability Detail</th>\n </tr>";

	private final static String TAIL = "</table>";

	private final static String CELL_BEGIN = "<tr><td>";
	private final static String CELL_END = "</td></tr>";

	@Override
	public StringBuilder listAbilityDetail(boolean html) {
		Map<String, Object> map = MessageRecvExecutor.getInstance()
				.getHandlerMap();
		ReflectionUtils utils = new ReflectionUtils();
		if (html) {
			utils.getProvider().append(STYLE).append(HEADER);
		}
		Set<String> s = map.keySet();
		Iterator<String> iter = s.iterator();
		String key;
		while (iter.hasNext()) {
			key = iter.next();
			try {
				if (html) {
					utils.getProvider().append(CELL_BEGIN);
					utils.listRpcProviderDetail(Class.forName(key), html);
					utils.getProvider().append(CELL_END);
				} else {
					utils.listRpcProviderDetail(Class.forName(key), html);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (html) {
			utils.getProvider().append(TAIL);
		}
		return utils.getProvider();
	}

}
