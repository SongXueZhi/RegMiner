import React, { createRef, useState } from 'react';
import { monaco, MonacoDiffEditor } from 'react-monaco-editor';
import { v4 as uuidv4 } from 'uuid';
import type { Depandency, Directory } from './sidebar';
import type { ResizeEntry } from '@blueprintjs/core';
import { Button, Divider, ResizeSensor } from '@blueprintjs/core';
import EllipsisMiddle from '../EllipsisMiddle';
import { Dropdown, Input, Menu, message, Space } from 'antd';
import type { DiffEditDetailItems, FeedbackList, HunkEntityItems } from '@/pages/editor/data';
import { DownOutlined } from '@ant-design/icons';
import { postRegressionRevert, postRegressionUpdateNewCode } from '@/pages/editor/service';
import { useAccess } from 'umi';

import './styles.css';

interface IProps {
  title: string;
  regressionUuid: string;
  filename: string;
  extra?: JSX.Element;
  oldVersionText?: string;
  newVersionText?: string;
  newPath: string;
  oldPath: string;
  darkTheme: boolean;
  dirs?: Directory[];
  depandencies?: Depandency[];
  original?: string;
  value?: string;
  diffEditChanges: DiffEditDetailItems[];
  isRunning: boolean;
  consoleString?: string;
  CriticalChange: HunkEntityItems | undefined;
  projectFullName: string;
  onRunCode?: (code: string, version: string, command?: string) => void;
  onFeedbackList?: (feedback: FeedbackList) => void;
  onRevertCode?: (
    commit: string,
    filename: string,
    oldPath: string,
    newPath: string,
    editList: DiffEditDetailItems[],
    CriticalChange: HunkEntityItems | undefined,
  ) => void;
}

const REVEAL_CONSOLE_HEIHGT = 31;
const DEFAULT_HEIGHT = 40;
const NewCodeEditor: React.FC<IProps> = ({
  title,
  regressionUuid,
  filename,
  extra,
  oldVersionText,
  newVersionText,
  newPath,
  oldPath,
  darkTheme,
  original,
  value,
  diffEditChanges,
  isRunning,
  consoleString,
  CriticalChange,
  projectFullName,
  onRunCode,
  onFeedbackList,
  onRevertCode,
}) => {
  const access = useAccess();
  const [inputValue, setInputValue] = useState('');
  const [showConsole, setShowConsole] = useState<boolean>(false);
  const [version, setVersion] = useState<string>('firstOp');
  // const [feedbackContextList, setFeedbackContextList] = useState<FeedbackList>();
  const [monacoSize, setMonacoSize] = useState<{
    width: string | number;
    height: string | number;
  }>({ width: 0, height: 0 });
  const [decorationIds, setDecorationIds] = useState<string[]>([]);
  const [modifiedNewCode, setModifiedNewCode] = useState<string | undefined>(value);
  const editorRef = createRef<MonacoDiffEditor>();
  const uuid = 'editor' + uuidv4();
  //   const editor: monacoEditor.editor.IStandaloneDiffEditor | undefined = editorRef.current?.editor;

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
  };
  const handleResizeMonacoEditor = (entries: ResizeEntry[]) => {
    const e = entries[0] as ResizeEntry;
    const width = e.contentRect.width;
    let height = (e.contentRect.height ?? DEFAULT_HEIGHT) - 40; // 固定减去 TitleView 的 40 高
    if (showConsole) {
      // 显示全部 ConsoleView
      const consoleView = document.querySelector('.ConsoleView');
      if (consoleView !== null) height -= (consoleView as HTMLElement).offsetHeight;
    } else {
      height -= 30;
    } // 显示部分 ConsoleView，固定为 30px
    setMonacoSize({ width: width, height: height });
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
    setMonacoSize({ width, height: nextH });
  };
  const handleRunClick = async (option: string) => {
    if (access.allUsersFoo) {
      setVersion(option);
      let content: string | undefined = (
        option === 'firstOp'
          ? editorRef.current?.editor?.getOriginalEditor()
          : editorRef.current?.editor?.getModifiedEditor()
      )?.getValue();
      const revisionFlag =
        option === 'firstOp' ? oldVersionText ?? 'firstOp' : newVersionText ?? 'secondOp';
      if (typeof content === 'undefined') content = '';

      // 判断inputValue是否为空，然后相应地调用onRunCode函数
      if (inputValue.trim() === '') {
        onRunCode?.call(this, content, revisionFlag);
      } else {
        onRunCode?.call(this, content, revisionFlag, inputValue);
      }

      if (!showConsole) {
        handleShowConsole();
      }
    } else {
      message.error('Sorry, you have no right to do that. Please login or use another account!');
    }
  };

  const handlefeedbackList = (
    key: string[],
    revision: 'bic' | 'bfc',
    fileName: string,
    feedback: string,
    range: monaco.Selection,
    diffDetail: DiffEditDetailItems,
  ) => {
    onFeedbackList?.call(onFeedbackList, {
      decorationKey: key,
      revision: revision,
      fileName: fileName,
      feedback: feedback,
      hunkData: {
        newPath: newPath,
        oldPath: oldPath,
        beginA: diffDetail.beginA ?? range.startLineNumber,
        beginB: diffDetail.beginB ?? range.startLineNumber,
        endA: diffDetail.endA ?? range.endLineNumber,
        endB: diffDetail.endB ?? range.endLineNumber,
        type: diffDetail.type ?? '',
      },
    });
  };

  const handleDiffEditorOnChange = async (
    v: string,
    // content: monaco.editor.IModelContentChangedEvent,
  ) => {
    setModifiedNewCode(v);
  };
  const handleUpdateNewCodeClick = async () => {
    if (access.allUsersFoo) {
      const newCode = modifiedNewCode;
      postRegressionUpdateNewCode(
        {
          projectFullName: projectFullName,
          userToken: '123',
          regressionUuid: regressionUuid,
          filePath: oldPath,
          revisionName: title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
        },
        newCode ?? ' ',
      )
        .then(() => {
          onRevertCode?.call(
            // eslint-disable-next-line @typescript-eslint/no-invalid-this
            this,
            title === 'Bug Inducing Commit' ? 'BIC' : 'BFC',
            filename,
            oldPath,
            newPath,
            diffEditChanges,
            undefined,
          );
        })
        .catch(() => {
          message.error('Failed to update, check the code again!');
        });
    } else {
      message.error('Sorry, you have no right to do that. Please login or use another account!');
    }
  };
  const handleRevertCode = async () => {
    if (access.allUsersFoo) {
      await postRegressionRevert({
        projectFullName: projectFullName,
        regressionUuid: regressionUuid,
        userToken: '123',
        filePath: oldPath,
        revisionName: title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
      })
        .then(() => {
          // message.success('Code reverted!');
          onRevertCode?.call(
            // eslint-disable-next-line @typescript-eslint/no-invalid-this
            this,
            title === 'Bug Inducing Commit' ? 'BIC' : 'BFC',
            filename,
            oldPath,
            newPath,
            diffEditChanges,
            undefined,
          );
        })
        .catch(() => {
          message.error('Revert failed, please try again!');
        });
    } else {
      message.error('Sorry, you have no right to do that. Please login or use another account!');
    }
  };

  return (
    // @ts-ignore
    <ResizeSensor onResize={handleResizeMonacoEditor}>
      <div className="EditorRoot" id={uuid}>
        <div
          className={darkTheme ? 'TitlebarView flex between dark' : 'TitlebarView flex between'}
          // style={{ width: '100%' }}
          style={{ width: '100%', display: 'inline-flex' }}
        >
          <div className="project-title">
            <EllipsisMiddle suffixCount={12}>{title}</EllipsisMiddle>
          </div>
          <div className="update-new-code-btn">
            <Button
              id="revert-new-code-btn"
              data-imitate
              style={{ height: '30px' }}
              intent="success"
              icon="upload"
              onClick={handleUpdateNewCodeClick}
              disabled={modifiedNewCode === value}
            >
              Update Code
            </Button>
          </div>
          <div className="revert-code-btn">
            <Button
              id="revert-new-code-btn"
              data-imitate
              style={{ height: '30px' }}
              intent="success"
              icon="repeat"
              onClick={handleRevertCode}
            >
              Revert
            </Button>
          </div>
          <div className="run-button" style={{ display: 'flex', alignItems: 'center' }}>
            {extra}
            <Input
              placeholder="命令行输入"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              style={{ marginRight: 20 }}
            />
            <Dropdown
              overlay={
                <Menu
                  selectable
                  defaultSelectedKeys={[version]}
                  onClick={(v) => handleRunClick(v.key)}
                  items={[
                    {
                      label: (
                        <span style={{ fontSize: 16, fontWeight: 'bolder' }}>
                          {oldVersionText ?? 'firstOp'}
                        </span>
                      ),
                      key: 'firstOp',
                    },
                    {
                      label: (
                        <span style={{ fontSize: 16, fontWeight: 'bolder' }}>
                          {newVersionText ?? 'secondOp'}
                        </span>
                      ),
                      key: 'secondOp',
                    },
                    {
                      label: (
                        <span style={{ fontSize: 16, fontWeight: 'bolder' }}>
                          bug introduce with new code
                        </span>
                      ),
                      key: 'thirdOp',
                      disabled: true,
                    },
                  ]}
                />
              }
            >
              <Button intent="success" icon="play" loading={isRunning}>
                <Space>
                  Run
                  <DownOutlined />
                </Space>
              </Button>
            </Dropdown>
          </div>
        </div>
        <div className="EditorView">
          <MonacoDiffEditor
            ref={editorRef}
            width={monacoSize.width}
            height={monacoSize.height}
            language={'java'}
            theme={darkTheme ? 'vs-dark' : 'vs-light'}
            options={options}
            original={original}
            value={modifiedNewCode}
            onChange={handleDiffEditorOnChange}
            editorDidMount={(diffEditor) => {
              if (CriticalChange !== undefined) {
                const codeEditor = diffEditor.getModifiedEditor();
                diffEditor.revealLineInCenter(
                  CriticalChange.beginB - 10 >= 0
                    ? CriticalChange.beginB - 10
                    : CriticalChange.beginB,
                );
                codeEditor.deltaDecorations(
                  [],
                  [
                    {
                      range: new monaco.Range(CriticalChange.beginB, 0, CriticalChange.endB, 0),
                      options: {
                        isWholeLine: true,
                        className: 'criticalChangeHintClass',
                      },
                    },
                  ],
                );
              }
              diffEditor.addAction({
                id: 'feedback-add',
                label: 'feedback: add',
                keybindingContext: undefined,
                contextMenuGroupId: 'navigation',
                contextMenuOrder: 1,
                run: (ed) => {
                  const selectionRange = ed.getSelection();
                  if (selectionRange) {
                    const diffDetail = diffEditChanges.find(
                      (resp) =>
                        (selectionRange.startLineNumber <= resp.beginB &&
                          selectionRange.endLineNumber >= resp.beginB) ||
                        (selectionRange.startLineNumber >= resp.beginB &&
                          selectionRange.startLineNumber <= resp.endB),
                    );
                    const oldDecorations = ed.getDecorationsInRange(selectionRange);
                    if (oldDecorations !== null && diffDetail !== undefined) {
                      if (decorationIds.some((d) => d === oldDecorations[0].id)) {
                        const newIdList = decorationIds.filter((v) => v !== oldDecorations[0].id);
                        const newDecoration = ed.deltaDecorations(
                          [oldDecorations[0].id],
                          [
                            {
                              range: selectionRange,
                              options: {
                                className: 'addContentClass',
                                hoverMessage: { value: 'Feedback: Add' },
                              },
                            },
                          ],
                        );
                        setDecorationIds(
                          newIdList.splice(newIdList.length + 1, 0, newDecoration.toString()),
                        );
                        handlefeedbackList(
                          newDecoration,
                          title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                          filename,
                          'add',
                          selectionRange,
                          diffDetail,
                        );
                      } else {
                        const newDecoration = ed.deltaDecorations(
                          [],
                          [
                            {
                              range: selectionRange,
                              options: {
                                className: 'addContentClass',
                                hoverMessage: { value: 'Feedback: Add' },
                              },
                            },
                          ],
                        );
                        setDecorationIds(
                          decorationIds.splice(
                            decorationIds.length + 1,
                            0,
                            newDecoration.toString(),
                          ),
                        );
                        // console.log(diffDetail);
                        // console.log(selectionRange);
                        // console.log(newDecoration);
                        // console.log(filename);
                        // console.log(title);
                        handlefeedbackList(
                          newDecoration,
                          title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                          filename,
                          'add',
                          selectionRange,
                          diffDetail,
                        );
                      }
                    } else {
                      message.warning('The code you select does not have hunk information!');
                    }
                  } else {
                    console.log('nothing');
                  }
                },
              });
              diffEditor.addAction({
                id: 'feedback-reject',
                label: 'feedback: reject',
                keybindingContext: undefined,
                contextMenuGroupId: 'navigation',
                contextMenuOrder: 2,
                run: (ed) => {
                  const selectionRange = ed.getSelection();
                  if (selectionRange) {
                    const diffDetail = diffEditChanges.find(
                      (resp) =>
                        (selectionRange.startLineNumber <= resp.beginB &&
                          selectionRange.endLineNumber >= resp.beginB) ||
                        (selectionRange.startLineNumber >= resp.beginB &&
                          selectionRange.startLineNumber <= resp.endB),
                    );
                    const oldDecorations = ed.getDecorationsInRange(selectionRange);
                    if (oldDecorations !== null && diffDetail !== undefined) {
                      if (decorationIds.some((d) => d === oldDecorations[0].id)) {
                        const newIdList = decorationIds.filter((v) => v !== oldDecorations[0].id);
                        const newDecoration = ed.deltaDecorations(
                          [oldDecorations[0].id],
                          [
                            {
                              range: selectionRange,
                              options: {
                                className: 'rejectContentClass',
                                hoverMessage: { value: 'Feedback: Reject' },
                              },
                            },
                          ],
                        );
                        setDecorationIds(
                          newIdList.splice(newIdList.length + 1, 0, newDecoration.toString()),
                        );
                        handlefeedbackList(
                          newDecoration,
                          title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                          filename,
                          'reject',
                          selectionRange,
                          diffDetail,
                        );
                      } else {
                        const newDecoration = ed.deltaDecorations(
                          [],
                          [
                            {
                              range: selectionRange,
                              options: {
                                className: 'rejectContentClass',
                                hoverMessage: { value: 'Feedback: Reject' },
                              },
                            },
                          ],
                        );
                        setDecorationIds(
                          decorationIds.splice(
                            decorationIds.length + 1,
                            0,
                            newDecoration.toString(),
                          ),
                        );
                        handlefeedbackList(
                          newDecoration,
                          title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                          filename,
                          'reject',
                          selectionRange,
                          diffDetail,
                        );
                      }
                    } else {
                      message.warning('The code you select does not have hunk information!');
                    }
                  } else {
                    console.log('nothing');
                  }
                },
              });
              diffEditor.addAction({
                id: 'feedback-confirm-ground-truth',
                label: 'feedback: ground truth',
                keybindingContext: undefined,
                contextMenuGroupId: 'navigation',
                contextMenuOrder: 3,
                run: (ed) => {
                  const selectionRange = ed.getSelection();
                  if (selectionRange) {
                    const diffDetail = diffEditChanges.find(
                      (resp) =>
                        (selectionRange.startLineNumber <= resp.beginB &&
                          selectionRange.endLineNumber >= resp.beginB) ||
                        (selectionRange.startLineNumber >= resp.beginB &&
                          selectionRange.startLineNumber <= resp.endB),
                    );
                    const oldDecorations = ed.getDecorationsInRange(selectionRange);
                    if (oldDecorations !== null && diffDetail !== undefined) {
                      if (decorationIds.some((d) => d === oldDecorations[0].id)) {
                        const newIdList = decorationIds.filter((v) => v !== oldDecorations[0].id);
                        const newDecoration = ed.deltaDecorations(
                          [oldDecorations[0].id],
                          [
                            {
                              range: selectionRange,
                              options: {
                                className: 'confirmContentClass',
                                hoverMessage: { value: 'Feedback: Ground Truth' },
                              },
                            },
                          ],
                        );
                        setDecorationIds(
                          newIdList.splice(newIdList.length + 1, 0, newDecoration.toString()),
                        );
                        handlefeedbackList(
                          newDecoration,
                          title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                          filename,
                          'ground truth',
                          selectionRange,
                          diffDetail,
                        );
                      } else {
                        const newDecoration = ed.deltaDecorations(
                          [],
                          [
                            {
                              range: selectionRange,
                              options: {
                                className: 'confirmContentClass',
                                hoverMessage: { value: 'Feedback: Confirm' },
                              },
                            },
                          ],
                        );
                        setDecorationIds(
                          decorationIds.splice(
                            decorationIds.length + 1,
                            0,
                            newDecoration.toString(),
                          ),
                        );
                        handlefeedbackList(
                          newDecoration,
                          title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                          filename,
                          'ground truth',
                          selectionRange,
                          diffDetail,
                        );
                      }
                    } else {
                      message.warning('The code you select does not have hunk information!');
                    }
                  } else {
                    console.log('nothing');
                  }
                },
              });
            }}
          />
        </div>
        <div
          className={showConsole ? 'ConsoleView open' : 'ConsoleView'}
          style={
            darkTheme
              ? { backgroundColor: 'var(--dark-console-color)' }
              : { backgroundColor: 'var(--light-console-color)' }
          }
        >
          <Divider className={darkTheme ? 'divider dark' : 'divider'} />
          <section className="flex vertical" style={{ width: '100%', height: '97%' }}>
            <div className="header flex between none" onClick={handleShowConsole}>
              <div className="title" style={{ fontSize: '16px', fontWeight: 'bolder' }}>
                Console {version === 'firstOp' ? oldVersionText : newVersionText}
              </div>
              <div className="tools">
                <Button minimal icon={showConsole ? 'chevron-down' : 'chevron-up'} />
              </div>
            </div>
            <div id="logsFlow" className="Logs">
              <pre className="log output" style={{ overflow: 'unset' }}>
                {consoleString}
              </pre>
            </div>
          </section>
        </div>
      </div>
    </ResizeSensor>
  );
};

export default NewCodeEditor;
