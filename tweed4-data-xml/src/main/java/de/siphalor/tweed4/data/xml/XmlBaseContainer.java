package de.siphalor.tweed4.data.xml;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public interface XmlBaseContainer {
	default void setComment(Element element, @Nullable String comment) {
		Node previousSibling = element.getPreviousSibling();
		if (previousSibling instanceof Comment) {
			previousSibling.setTextContent(comment);
		} else {
			Comment xmlComment = element.getOwnerDocument().createComment(comment);
			element.getParentNode().insertBefore(xmlComment, element);
		}
	}

	default String getComment(Element element) {
		Node previousSibling = element.getPreviousSibling();
		if (previousSibling instanceof Comment) {
			return previousSibling.getTextContent();
		} else {
			return null;
		}
	}
}
