import React, { useState, useRef, useCallback } from "react";
import { useEditable } from "use-editable";
import Highlight, { defaultProps } from "prism-react-renderer";

const Editor = ({ children, onChange, language }) => {
  const editorRef = useRef(null);
  const [content, setContent] = useState(children);

  const onEditableChange = useCallback((text) => {
    // there is a new line at the end of text
    const textWithoutNewLine = text.slice(0, -1);
    setContent(textWithoutNewLine);
    onChange(textWithoutNewLine);
  }, []);

  useEditable(editorRef, onEditableChange, { indentation: 2 });

  return (
    <Highlight {...defaultProps} code={content} language={language}>
      {({ className, style, tokens, getTokenProps }) => (
        <pre className={className} style={style} ref={editorRef}>
          {tokens.map((line, i) => (
            <React.Fragment key={i}>
              {line
                .filter((token) => !token.empty)
                .map((token, key) => (
                  <span {...getTokenProps({ token, key })} />
                ))}
              {"\n"}
            </React.Fragment>
          ))}
        </pre>
      )}
    </Highlight>
  );
};

export default Editor;
