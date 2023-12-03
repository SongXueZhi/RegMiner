import type {RegressionCode} from '@/pages/editor/data';
import {getRegressionConsole, getRegressionPath, queryRegressionCode,} from '@/pages/editor/service';
import {Checkbox, Divider} from 'antd';
import type {CheckboxValueType} from 'antd/lib/checkbox/Group';
import React, {createRef, useCallback, useEffect, useState} from 'react';
import {monaco, MonacoDiffEditor} from 'react-monaco-editor';
import type {HunkEntityItems} from '../data';
import {v4 as uuidv4} from 'uuid';
import {Button} from '@blueprintjs/core';

// import '@Codeeditor/style.css';

export interface HunkCodeItems extends RegressionCode, HunkEntityItems {
  key: number;
}

interface IProps {
  regressionUuid?: string;
  revision?: string;
  allHunkInfo?: HunkEntityItems[];
  // selectedHunkIdx: number[];
  onSelectedHunks?: (selectedHunksIdx: number[]) => void;
}

// const DEFAULT_HEIGHT = 40;
const REVEAL_CONSOLE_HEIHGT = 31;

const DeltaDebuggingHunkBlocks: React.FC<IProps> = ({
                                                      regressionUuid,
                                                      revision,
                                                      allHunkInfo,
                                                      // selectedHunkIdx,
                                                      onSelectedHunks,
                                                    }) => {
  const [hunkCodeList, setHunkCodeList] = useState<HunkCodeItems[]>([]);
  const [showConsole, setShowConsole] = useState<boolean>(false);
  const [consoleResult, setConsoleResult] = useState<string>();
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [monacoSize, setMonacoSize] = useState<{
    width: string | number;
    height: string | number;
  }>({width: 1180, height: 300});
  const editorRef = createRef<MonacoDiffEditor>();
  const uuid = 'editor' + uuidv4();
  const options = {
    renderSideBySide: false,
    minimap: {enabled: false},
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
    fontSize: 10,
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
    const hunkIdx: number[] = value.map((d) => Number(d));
    // console.log(hunkIdx);
    onSelectedHunks?.call(onSelectedHunks, hunkIdx);
  };

  const handleShowConsole = () => {
    const width = monacoSize.width as number;
    const height = monacoSize.height as number;
    const consoleHeight = document.querySelector(`#${uuid} .ConsoleView`)?.clientHeight ?? 0;
    let nextH;
    if (showConsole) nextH = height + consoleHeight - REVEAL_CONSOLE_HEIHGT;
    // true => false
    else nextH = height - consoleHeight + REVEAL_CONSOLE_HEIHGT; // false => true
    setShowConsole(!showConsole);
    setMonacoSize({width, height: nextH});
  };

  const getConsoleResult = async (params: {
    regression_uuid: string;
    revisionFlag: string; // work | bic | buggy | bfc
    userToken: string;
  }) => {
    if (params.revisionFlag === 'work') {
      const path = await getRegressionPath({
        regression_uuid: params.regression_uuid,
        revisionFlag: params.revisionFlag,
        userToken: '123',
      }).then((resp) => {
        if (resp !== null && resp !== undefined) {
          return resp;
        } else {
          return null;
        }
      });
      if (path !== null && path !== undefined) {
        setIsRunning(true);
        while (true) {
          const data = await getRegressionConsole({path: path});
          await wait(500);
          setConsoleResult(data ?? '');
          if (data && data.includes('REGMINER-TEST-END')) {
            break;
          }
        }
        setIsRunning(false);
      }
    }
    // if (params.revisionFlag === 'bug introduce') {
    //   const path = await getRegressionPath({
    //     regression_uuid: params.regression_uuid,
    //     revisionFlag: 'bic',
    //     userToken: '123',
    //   }).then((resp) => {
    //     if (resp !== null && resp !== undefined) {
    //       return resp;
    //     } else {
    //       return null;
    //     }
    //   });
    //   if (path !== null && path !== undefined) {
    //     setBICIsRunning(true);
    //     while (true) {
    //       const data = await getRegressionConsole({ path: path });
    //       await wait(500);
    //       setBICConsoleResult(data ?? '');
    //       if (data && data.includes('REGMINER-TEST-END')) {
    //         break;
    //       }
    //     }
    //     setBICIsRunning(false);
    //   }
    // }
    // if (params.revisionFlag === 'buggy') {
    //   await wait(500);
    //   const path = await getRegressionPath({
    //     regression_uuid: params.regression_uuid,
    //     revisionFlag: params.revisionFlag,
    //     userToken: '123',
    //   }).then((resp) => {
    //     if (resp !== null && resp !== undefined) {
    //       return resp;
    //     } else {
    //       return null;
    //     }
    //   });
    //   if (path !== null && path !== undefined) {
    //     setBFCIsRunning(true);
    //     while (true) {
    //       await wait(1000);
    //       const data = await getRegressionConsole({ path: path });
    //       setBFCConsoleResult(data ?? '');
    //       if (data && data.includes('REGMINER-TEST-END')) {
    //         break;
    //       }
    //     }
    //     setBFCIsRunning(false);
    //   }
    // }
    // if (params.revisionFlag === 'bug fix') {
    //   await wait(500);
    //   const path = await getRegressionPath({
    //     regression_uuid: params.regression_uuid,
    //     revisionFlag: 'bfc',
    //     userToken: '123',
    //   }).then((resp) => {
    //     if (resp !== null && resp !== undefined) {
    //       return resp;
    //     } else {
    //       return null;
    //     }
    //   });
    //   if (path !== null && path !== undefined) {
    //     setBFCIsRunning(true);
    //     while (true) {
    //       await wait(1000);
    //       const data = await getRegressionConsole({ path: path });
    //       setBFCConsoleResult(data ?? '');
    //       if (data && data.includes('REGMINER-TEST-END')) {
    //         break;
    //       }
    //     }
    //     setBFCIsRunning(false);
    //   }
    // }
  };

  // 计时器
  function wait(ms: number) {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(true);
      }, ms);
    });
  }

  const handleHunkRunClick = useCallback(
    async (content, version) => {
      setConsoleResult('');
      const Result = getConsoleResult({
        regression_uuid: regressionUuid ?? '',
        revisionFlag: version,
        userToken: '123',
      }).then((resp) => resp);
      return Result;
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [getConsoleResult],
  );

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
                <br/>
                <div className="EditorView">
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
                </div>
                <div
                  className={showConsole ? 'ConsoleView open' : 'ConsoleView'}
                  style={{backgroundColor: 'var(--light-console-color)'}}
                >
                  <Divider className={'divider'}/>
                  {/* <Button onClick={() => handleHunkRunClick} /> */}
                  <section className="flex vertical" style={{width: '100%', height: '97%'}}>
                    <div className="header flex between none" onClick={handleShowConsole}>
                      <div className="title" style={{fontSize: '16px', fontWeight: 'bolder'}}>
                        Console
                      </div>
                      <Button
                        intent="success"
                        icon="play"
                        loading={isRunning}
                        onClick={() => handleHunkRunClick}
                      >
                        Run Code
                      </Button>
                      <div className="tools">
                        <Button minimal icon={showConsole ? 'chevron-down' : 'chevron-up'}/>
                      </div>
                    </div>
                    <div id="logsFlow" className="Logs">
                        <pre className="log output" style={{overflow: 'unset'}}>
                          {consoleResult}
                        </pre>
                    </div>
                  </section>
                </div>
              </Checkbox>
              <Divider/>
            </>
          );
        })
        : null}
    </Checkbox.Group>
    // </ResizeSensor>
  );
};

export default DeltaDebuggingHunkBlocks;
