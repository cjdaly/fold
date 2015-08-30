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

import java.io.PrintWriter;

public class HtmlComposer {

	private StringBuilder _builder;
	private PrintWriter _writer;

	public HtmlComposer(PrintWriter writer) {
		_writer = writer;
		_builder = new StringBuilder();
	}

	public String A(String href, String linkText) {
		_builder.setLength(0);
		_builder.append("<a href='");
		_builder.append(href);
		_builder.append("'>");
		_builder.append(linkText);
		_builder.append("</a>");
		return _builder.toString();
	}

	public void a(String href, String linkText) {
		_writer.print("<a href='");
		_writer.print(href);
		_writer.print("'>");
		_writer.print(linkText);
		_writer.println("</a>");
	}

	public void b(String text) {
		simpleTag("b", text);
	}

	public void br() {
		_writer.println("<br>");
	}

	public void button(String type, String text) {
		_writer.print("<button type='");
		_writer.print(type);
		_writer.print("'>");
		_writer.print(text);
		_writer.print("</button>");
	}

	public void code(String text) {
		simpleTag("code", text);
	}

	public void dd(String text) {
		simpleTag("dd", text);
	}

	public void div() {
		div(true);
	}

	public void div(boolean startTag) {
		simpleTag("div", startTag);
	}

	public void dl() {
		dl(true);
	}

	public void dl(boolean startTag) {
		simpleTag("dl", startTag);
	}

	public void dt(String text) {
		simpleTag("dt", text);
	}

	public void em(String text) {
		simpleTag("em", text);
	}

	public void form() {
		form(false);
	}

	public void form(boolean startTag) {
		simpleTag("form", startTag);
	}

	public void h(int level, String text) {
		if (level < 1)
			level = 1;
		else if (level > 6)
			level = 6;
		_writer.print("<h");
		_writer.print(level);
		_writer.print(">");
		_writer.print(text);
		_writer.print("</h");
		_writer.print(level);
		_writer.println(">");
	}

	public void html_head(String title) {
		_writer.println("<html>");
		_writer.println("<head>");
		_writer.print("<title>");
		_writer.print(title);
		_writer.println("</title>");
		_writer.println("</head>");
		_writer.println("<body>");
	}

	public void html_body(boolean startTag) {
		if (!startTag) {
			_writer.println("</body>");
			_writer.println("</html>");
		}
	}

	public void i(String text) {
		simpleTag("i", text);
	}

	public void img(String src, String alt) {
		_writer.print("<img src='");
		_writer.print(src);
		_writer.print("' alt='");
		_writer.print(alt);
		_writer.print("'>");
	}

	public void input(String type, String name) {
		_writer.print("<input type='");
		_writer.print(type);
		_writer.print("' name='");
		_writer.print(name);
		_writer.println("'>");
	}

	public void li(String text) {
		simpleTag("li", text);
	}

	public void object(String data, String type) {
		_writer.print("<object data='");
		_writer.print(data);
		_writer.print("' type='");
		_writer.print(type);
		_writer.print("'></object>");
	}

	public void ol() {
		ol(true);
	}

	public void ol(boolean startTag) {
		simpleTag("ol", startTag);
	}

	public void p() {
		p(true);
	}

	public void p(boolean startTag) {
		simpleTag("p", startTag);
	}

	public void p(String text) {
		simpleTag("p", text);
	}

	public void pre(String text) {
		simpleTag("pre", text);
	}

	public void span(String text) {
		simpleTag("span", text);
	}

	public void strong(String text) {
		simpleTag("strong", text);
	}

	public void table() {
		table(true);
	}

	public void table(boolean startTag) {
		simpleTag("table", startTag);
	}

	public void text(String text) {
		_writer.print(text);
	}

	public void td(String text) {
		simpleTag("td", text);
	}

	public void th(String text) {
		simpleTag("th", text);
	}

	public void tr(boolean startTag) {
		simpleTag("tr", startTag);
	}

	public void tr(String... tableCells) {
		tr(false, tableCells);
	}

	public void tr(boolean isHeader, String... tableCells) {
		_writer.print("<tr>");
		for (String tableCell : tableCells) {
			if (isHeader)
				_writer.print("<th>");
			else
				_writer.print("<td>");
			_writer.print(tableCell);
			if (isHeader)
				_writer.print("</th>");
			else
				_writer.print("</td>");
		}
		_writer.println("</tr>");
	}

	public void ul() {
		ul(true);
	}

	public void ul(boolean startTag) {
		simpleTag("ul", startTag);
	}

	//
	//

	private void simpleTag(String tag, String text) {
		_writer.print("<");
		_writer.print(tag);
		_writer.print(">");
		_writer.print(text);
		_writer.print("</");
		_writer.print(tag);
		_writer.println(">");
	}

	private void simpleTag(String tag, boolean startTag) {
		if (startTag) {
			_writer.print("<");
			_writer.print(tag);
			_writer.println(">");
		} else {
			_writer.print("</");
			_writer.print(tag);
			_writer.println(">");
		}
	}

}
