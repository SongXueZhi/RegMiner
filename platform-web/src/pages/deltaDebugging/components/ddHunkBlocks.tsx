import { Checkbox } from 'antd';
import { CheckboxValueType } from 'antd/lib/checkbox/Group';
import React, { createRef } from 'react';
import { MonacoDiffEditor } from 'react-monaco-editor';
import { ddInfoItems } from '../data';

interface IProps {
  ddHunkInfo: ddInfoItems;
}

const DeltaDebuggingHunkBlocks: React.FC<IProps> = ({ ddHunkInfo }) => {
  const editorRef = createRef<MonacoDiffEditor>();
  const options = {
    renderSideBySide: false,
    minimap: { enabled: false },
    scrollbar: {
      verticalScrollbarSize: 0,
      verticalSliderSize: 14,
      horizontalScrollbarSize: 0,
      horizontalSliderSize: 14,
      alwaysConsumeMouseWheel: false,
    },
    glyphMargin: true,
    folding: false,
    contextmenu: true,
    fontFamily: 'ui-monospace,SFMono-Regular,SF Mono,Menlo,Consolas,Liberation Mono,monospace',
    fontSize: 14,
    lineHeight: 20,
    extraEditorClassName: 'CodeEditor',
    //+ -指示器
    renderIndicators: true,
    // 原来的editor是否可编辑
    originalEditable: false,
    // line number width
    lineNumbersMinChars: 2,
  };

  const onChange = (value: CheckboxValueType[]) => {
    console.log('checkbox: ', value);
  };
  return (
    <Checkbox.Group onChange={onChange}>
      {ddHunkInfo.allHunks.map((data, index) => {
        return (
          <Checkbox value={data.hunkId} key={`${index}-${data.hunkId}`}>
            {data.hunkId}
            <MonacoDiffEditor
              ref={editorRef}
              width={600}
              height={200}
              language={'java'}
              theme={'vs-light'}
              options={options}
              original={data.oldCode}
              value={data.newCode}
            />
          </Checkbox>
        );
      })}
    </Checkbox.Group>
  );
};

export default DeltaDebuggingHunkBlocks;
