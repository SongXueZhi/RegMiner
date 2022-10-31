import { Checkbox, Col, Row } from 'antd';
import { CheckboxValueType } from 'antd/lib/checkbox/Group';
import React, { createRef } from 'react';
import { MonacoDiffEditor } from 'react-monaco-editor';
import { ddInfoItems } from '../data';

interface IProps {
  hunkInfo: ddInfoItems;
}

const DeltaDebuggingHunkBlocks: React.FC<IProps> = ({ hunkInfo }) => {
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
      <Row>
        {hunkInfo.allHunks.map((data) => {
          return (
            <Col span={24}>
              <Checkbox value={data.hunkId}>
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
            </Col>
          );
        })}
      </Row>
    </Checkbox.Group>
  );
};

export default DeltaDebuggingHunkBlocks;
