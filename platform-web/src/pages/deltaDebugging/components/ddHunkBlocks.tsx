import type { RegressionCode } from '@/pages/editor/data';
import { queryRegressionCode } from '@/pages/editor/service';
import { Checkbox, Col, Divider, Row } from 'antd';
import type { CheckboxValueType } from 'antd/lib/checkbox/Group';
import React, { createRef, useEffect, useState } from 'react';
import { MonacoDiffEditor } from 'react-monaco-editor';
import type { HunkEntityItems } from '../data';

export interface HunkCodeItems extends RegressionCode {
  key: number;
  newPath: string;
}

interface IProps {
  regressionUuid?: string;
  revision?: string;
  allHunkInfo?: HunkEntityItems[];
}

const DeltaDebuggingHunkBlocks: React.FC<IProps> = ({ regressionUuid, revision, allHunkInfo }) => {
  const [hunkCodeList, setHunkCodeList] = useState<HunkCodeItems[]>([]);

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
  const onChange = (value: CheckboxValueType[]) => {};

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
              regressionUuid: data.regressionUuid,
              oldCode: data.oldCode,
              newCode: data.newCode,
              key: index,
              newPath: resp.newPath,
            };
            return hunkCode;
          }
          return null;
        });
        if (result !== null) {
          hunkCodeList.push(result);
        }
        setHunkCodeList(hunkCodeList);
      });
    }
  }, [allHunkInfo, hunkCodeList, regressionUuid, revision]);

  return (
    <>
      {/* {JSON.stringify(hunkCodeList)} */}
      <Checkbox.Group onChange={onChange}>
        {hunkCodeList
          ? hunkCodeList.map((data) => {
              return (
                <>
                  <Checkbox value={data.key}>
                    {'hunk ' + data.key}
                    <br />
                    <MonacoDiffEditor
                      ref={editorRef}
                      width={800}
                      height={200}
                      language={'java'}
                      theme={'vs-light'}
                      options={options}
                      original={data.oldCode}
                      value={data.newCode}
                    />
                  </Checkbox>
                  <Divider />
                </>
              );
            })
          : null}
      </Checkbox.Group>
    </>
  );
};

export default DeltaDebuggingHunkBlocks;
