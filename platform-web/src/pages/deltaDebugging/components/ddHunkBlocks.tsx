import type { RegressionCode } from '@/pages/editor/data';
import { queryRegressionCode } from '@/pages/editor/service';
import { ResizeEntry, ResizeSensor } from '@blueprintjs/core';
import { Checkbox, Col, Divider, Row } from 'antd';
import type { CheckboxValueType } from 'antd/lib/checkbox/Group';
import React, { createRef, useEffect, useState } from 'react';
import { monaco, MonacoDiffEditor } from 'react-monaco-editor';
import type { HunkEntityItems } from '../data';
// import '@Codeeditor/style.css';

export interface HunkCodeItems extends RegressionCode, HunkEntityItems {
  key: number;
}

interface IProps {
  regressionUuid?: string;
  revision?: string;
  allHunkInfo?: HunkEntityItems[];
  choosedHunksIndex?: number[];
}

// const DEFAULT_HEIGHT = 40;

const DeltaDebuggingHunkBlocks: React.FC<IProps> = ({ regressionUuid, revision, allHunkInfo }) => {
  const [hunkCodeList, setHunkCodeList] = useState<HunkCodeItems[]>([]);
  const [monacoSize, setMonacoSize] = useState<{
    width: string | number;
    height: string | number;
  }>({ width: 1180, height: 300 });
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

  // const handleResizeMonacoEditor = (entries: ResizeEntry[]) => {
  //   const e = entries[0] as ResizeEntry;
  //   const width = e.contentRect.width;
  //   const height = (e.contentRect.height ?? DEFAULT_HEIGHT) - 40; // 固定减去 TitleView 的 40 高

  //   setMonacoSize({ width: width, height: height });
  // };

  const onChange = (value: CheckboxValueType[]) => {
    console.log(value);
  };

  useEffect(() => {
    if (allHunkInfo && regressionUuid && revision) {
      allHunkInfo.map(async (resp, index) => {
        const result = await queryRegressionCode({
          regression_uuid: regressionUuid,
          userToken: '123',
          old_path: resp.oldPath,
          new_path: resp.newPath,
          revisionFlag: revision,
        }).then((data) => {
          if (data) {
            const hunkCode: HunkCodeItems = {
              key: index,
              regressionUuid: data.regressionUuid,
              oldCode: data.oldCode,
              newCode: data.newCode,
              ...resp,
            };
            return hunkCode;
          }
          return null;
        });
        if (result !== null) {
          hunkCodeList.push(result);
          setHunkCodeList(hunkCodeList);
        }
      });
    }
  }, [allHunkInfo, hunkCodeList, regressionUuid, revision]);

  return (
    // <ResizeSensor onResize={handleResizeMonacoEditor}>
    <Checkbox.Group onChange={onChange}>
      {hunkCodeList
        ? hunkCodeList.map((data) => {
            return (
              <>
                <Checkbox value={data.key}>
                  {'hunk ' + data.key}
                  <br />
                  <MonacoDiffEditor
                    key={data.key}
                    ref={editorRef}
                    width={monacoSize.width}
                    height={monacoSize.height}
                    language={'java'}
                    theme={'vs-light'}
                    options={options}
                    original={data.oldCode}
                    value={data.newCode}
                    editorDidMount={(diffEditor) => {
                      const codeEditor = diffEditor.getModifiedEditor();
                      diffEditor.revealLineInCenter(data.beginB);
                      codeEditor.deltaDecorations(
                        [],
                        [
                          {
                            range: new monaco.Range(data.beginB, 0, data.endB, 0),
                            options: {
                              isWholeLine: true,
                              className: 'criticalChangeHintClass',
                            },
                          },
                        ],
                      );
                    }}
                  />
                </Checkbox>
                <Divider />
              </>
            );
          })
        : null}
    </Checkbox.Group>
    // </ResizeSensor>
  );
};

export default DeltaDebuggingHunkBlocks;
