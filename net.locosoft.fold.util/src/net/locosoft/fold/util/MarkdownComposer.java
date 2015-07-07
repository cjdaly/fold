/*****************************************************************************
 * Copyright (c) 2015 Chris J Daly (github user cjdaly)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   cjdaly - initial API and implementation
 ****************************************************************************/

package net.locosoft.fold.util;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.WriterConfig;
import com.github.rjeschke.txtmark.Processor;

public class MarkdownComposer {

	private StringBuilder _markdown = new StringBuilder();

	public void line(String line, boolean lineBreak) {
		_markdown.append(line);
		if (lineBreak)
			_markdown.append("\n\n");
		else
			_markdown.append('\n');
	}

	public void line(String line) {
		line(line, false);
	}

	public void line() {
		_markdown.append('\n');
	}

	public void table(boolean startTag) {
		if (startTag)
			_markdown.append("\n<table>\n");
		else
			_markdown.append("</table>\n");
	}

	public void table() {
		table(true);
	}

	public void tr(String... tds) {
		_markdown.append("<tr>");
		for (String td : tds) {
			_markdown.append("<td>");
			_markdown.append(td);
			_markdown.append("</td>");
		}
		_markdown.append("</tr>\n");
	}

	public void json(JsonObject jsonObject) {
		_markdown.append("<pre>");
		_markdown.append(jsonObject.toString(WriterConfig.PRETTY_PRINT));
		_markdown.append("</pre>\n\n");
	}

	public String makeA(String href, String linkText) {
		return "<a href='" + href + "'>" + linkText + "</a>";
	}

	public String getMarkdown() {
		return _markdown.toString();
	}

	public String getHtml() {
		return Processor.process(getMarkdown());
	}

}
