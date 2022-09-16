import { RefObject } from 'react';
import React, { createRef } from 'react';
import { monaco, MonacoDiffEditor } from 'react-monaco-editor';
import { v4 as uuidv4 } from 'uuid';
import type * as monacoEditor from 'monaco-editor/esm/vs/editor/editor.api';
import type { Directory, Depandency } from './sidebar.d';
import { ResizeSensor, Divider, Button, ResizeEntry } from '@blueprintjs/core';
import './styles.css';
import EllipsisMiddle from '../EllipsisMiddle';
import { Dropdown, Menu, message, Space } from 'antd';
import { Modal } from 'antd';
import CodeDetails from '../CodeDetails';
import type { DiffEditDetailItems, FeedbackList, HunkEntityItems } from '@/pages/editor/data';
import { postRegressionCodeModified } from '@/pages/editor/service';
import { DownOutlined } from '@ant-design/icons';

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
  onRunCode?: (code: string, version: string) => void;
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
interface IState {
  showConsole: boolean;
  showCodeDetails: boolean;
  feedbackContextList: FeedbackList;
  version: 'firstOp' | 'secondOp';
  testversion: string;
  consoleString?: string | null;
  monacoSize: { width: string | number; height: string | number };
  decorationIds: string[];
  modifiedFlag: boolean;
  modifiedNewCode?: string;
}

const REVEAL_CONSOLE_HEIHGT = 31;
const DEFAULT_HEIGHT = 40;
class CodeEditor extends React.Component<IProps, IState> {
  private options = {
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
  private uuid: string = '';
  private editorRef: RefObject<MonacoDiffEditor> = createRef<MonacoDiffEditor>();
  public editor: monacoEditor.editor.IStandaloneDiffEditor | undefined = undefined;
  constructor(props: IProps) {
    super(props);
    this.state = {
      showConsole: false,
      showCodeDetails: false,
      feedbackContextList: {
        decorationKey: [],
        revision: 'bic',
        fileName: '',
        feedback: '',
        hunkData: {
          newPath: '',
          oldPath: '',
          beginA: 0,
          beginB: 0,
          endA: 0,
          endB: 0,
          type: '',
        },
      },
      version: 'firstOp',
      testversion: 'firstOp',
      monacoSize: { width: 0, height: 0 },
      decorationIds: [],
      modifiedFlag: false,
      modifiedNewCode: props.value,
    };
  }
  componentDidMount() {
    this.uuid = 'editor' + uuidv4();
    this.editor = this.editorRef.current?.editor;
  }

  private handleResizeMonacoEditor = (entries: ResizeEntry[]) => {
    const e = entries[0] as ResizeEntry;
    const width = e.contentRect.width;
    let height = (e.contentRect.height ?? DEFAULT_HEIGHT) - 40; // 固定减去 TitleView 的 40 高
    if (this.state.showConsole) {
      // 显示全部 ConsoleView
      const consoleView = document.querySelector('.ConsoleView');
      if (consoleView !== null) height -= (consoleView as HTMLElement).offsetHeight;
    } else {
      height -= 30;
    } // 显示部分 ConsoleView，固定为 30px
    this.setState({ monacoSize: { width, height } });
  };
  // private handleVersionChange = ({ target }: RadioChangeEvent) => {
  //   this.setState({
  //     version: target.value as 'firstOp' | 'secondOp',
  //   });
  // };
  // private handleRunClick = async () => {
  //   let content: string | undefined = (
  //     this.state.version === 'firstOp'
  //       ? this.editorRef.current?.editor?.getOriginalEditor()
  //       : this.editorRef.current?.editor?.getModifiedEditor()
  //   )?.getValue();
  //   const version =
  //     this.state.version === 'firstOp'
  //       ? this.props.oldVersionText ?? 'firstOp'
  //       : this.props.newVersionText ?? 'secondOp';
  //   if (typeof content === 'undefined') content = '';
  //   this.props.onRunCode?.call(this, content, version);
  //   if (!this.state.showConsole) {
  //     this.handleShowConsole();
  //   }
  // };
  private handleRunClick = async (option: string) => {
    let content: string | undefined = (
      option === 'firstOp'
        ? this.editorRef.current?.editor?.getOriginalEditor()
        : this.editorRef.current?.editor?.getModifiedEditor()
    )?.getValue();
    const version =
      option === 'firstOp'
        ? this.props.oldVersionText ?? 'firstOp'
        : this.props.newVersionText ?? 'secondOp';
    if (typeof content === 'undefined') content = '';
    this.props.onRunCode?.call(this, content, version);
    if (!this.state.showConsole) {
      this.handleShowConsole();
    }
  };
  private handleShowConsole = () => {
    const width = this.state.monacoSize.width as number;
    const height = this.state.monacoSize.height as number;
    const consoleHeight = document.querySelector(`#${this.uuid} .ConsoleView`)?.clientHeight ?? 0;
    let nextH;
    if (this.state.showConsole) nextH = height + consoleHeight - REVEAL_CONSOLE_HEIHGT;
    // true => false
    else nextH = height - consoleHeight + REVEAL_CONSOLE_HEIHGT; // false => true
    this.setState({
      showConsole: !this.state.showConsole,
      monacoSize: { width, height: nextH },
    });
  };
  private handlefeedbackList = async (
    key: string[],
    revision: 'bic' | 'bfc',
    fileName: string,
    feedback: string,
    range: monaco.Selection,
    hunkData: DiffEditDetailItems,
  ) => {
    this.setState({
      feedbackContextList: {
        decorationKey: key,
        revision: revision,
        fileName: fileName,
        feedback: feedback,
        hunkData: {
          newPath: this.props.newPath,
          oldPath: this.props.oldPath,
          beginA: hunkData.beginA ?? range.startLineNumber,
          beginB: hunkData.beginB ?? range.startLineNumber,
          endA: hunkData.endA ?? range.endLineNumber,
          endB: hunkData.endB ?? range.endLineNumber,
          type: hunkData.type ?? '',
        },
      },
    });
    this.props.onFeedbackList?.call(this, this.state.feedbackContextList);
  };
  private handleDiffEditorOnChange = async (
    v: string,
    // content: monaco.editor.IModelContentChangedEvent,
  ) => {
    this.setState({ modifiedNewCode: v });
  };
  private handleUpdateNewCodeClick = async () => {
    // console.log('update code');
    // console.log(this.state.modifiedNewCode);
    const newCode = this.state.modifiedNewCode;
    postRegressionCodeModified(
      {
        userToken: '123',
        regression_uuid: this.props.regressionUuid,
        old_path: this.props.oldPath,
        revision_name: this.props.title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
        cover_status: 1,
      },
      newCode ?? ' ',
    )
      .then(() => {
        message.success('Code updated');
      })
      .catch(() => {
        message.error('Failed to update, check the code again!');
      });
  };
  private handleRevertCode = async () => {
    const newCode = ' ';
    await postRegressionCodeModified(
      {
        regression_uuid: this.props.regressionUuid,
        userToken: '123',
        old_path: this.props.oldPath,
        revision_name: this.props.title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
        cover_status: 0,
      },
      newCode,
    )
      .then(() => {
        message.success('Code reverted!');
        this.props.onRevertCode?.call(
          this,
          this.props.title === 'Bug Inducing Commit' ? 'BIC' : 'BFC',
          this.props.filename,
          this.props.oldPath,
          this.props.newPath,
          this.props.diffEditChanges,
          undefined,
        );
      })
      .catch(() => {
        message.error('Revert failed, please try again!');
      });
  };
  render() {
    const {
      regressionUuid,
      filename,
      darkTheme,
      original,
      title,
      extra,
      diffEditChanges,
      oldVersionText,
      newVersionText,
      consoleString,
      isRunning,
      CriticalChange,
    } = this.props;
    const { showConsole, version, showCodeDetails, decorationIds, modifiedNewCode } = this.state;
    const { width, height } = this.state.monacoSize;
    const logs = (
      <pre className="log output" style={{ overflow: 'unset' }}>
        {consoleString}
      </pre>
    );
    return (
      <>
        <ResizeSensor onResize={this.handleResizeMonacoEditor}>
          <div className="EditorRoot" id={this.uuid}>
            <div
              className={darkTheme ? 'TitlebarView flex between dark' : 'TitlebarView flex between'}
              // style={{ width: '100%' }}
              style={{ width: '100%', display: 'inline-flex' }}
            >
              <div className="project-title">
                <EllipsisMiddle suffixCount={12}>{title}</EllipsisMiddle>
              </div>
              <div className="regression-file-details-btn">
                <Button
                  id="show-code-details"
                  icon="search"
                  intent="primary"
                  style={{ marginLeft: '5px' }}
                  onClick={() => this.setState({ showCodeDetails: true })}
                >
                  Details
                </Button>
              </div>
              <div className="update-new-code-btn">
                <Button
                  id="revert-new-code-btn"
                  data-imitate
                  style={{ height: '30px' }}
                  intent="success"
                  icon="upload"
                  onClick={this.handleUpdateNewCodeClick}
                  disabled={modifiedNewCode === this.props.value}
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
                  onClick={this.handleRevertCode}
                >
                  Revert
                </Button>
              </div>
              <div className="run-button">
                {extra}
                {/* <Button
                  id="run-code-btn"
                  data-imitate
                  style={{ height: '30px', marginRight: '5px' }}
                  intent="success"
                  icon="play"
                  onClick={this.handleRunClick}
                  loading={isRunning}
                >
                  Run
                </Button>
                <Radio.Group
                  value={version}
                  buttonStyle="solid"
                  onChange={this.handleVersionChange}
                >
                  <Radio value="firstOp">{oldVersionText ?? 'firstOp'}</Radio>
                  <Radio value="secondOp">{newVersionText ?? 'secondOp'}</Radio>
                </Radio.Group> */}
                <Dropdown
                  overlay={
                    <Menu
                      selectable
                      defaultSelectedKeys={[version]}
                      onClick={(v) => this.handleRunClick(v.key)}
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
                ref={this.editorRef}
                width={width}
                height={height}
                language={'java'}
                theme={darkTheme ? 'vs-dark' : 'vs-light'}
                options={this.options}
                original={original}
                value={modifiedNewCode}
                onChange={this.handleDiffEditorOnChange}
                editorDidMount={(diffEditor, diffMonaco) => {
                  if (CriticalChange !== undefined) {
                    console.log(CriticalChange.beginB);
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
                        const hunkData = diffEditChanges.find((resp) => {
                          if (
                            (selectionRange.startLineNumber <= resp.beginB &&
                              selectionRange.endLineNumber >= resp.beginB) ||
                            (selectionRange.startLineNumber >= resp.beginB &&
                              selectionRange.startLineNumber <= resp.endB)
                          ) {
                            return resp;
                          } else {
                            return undefined;
                          }
                        });
                        const oldDecorations = ed.getDecorationsInRange(selectionRange);
                        if (oldDecorations !== null && hunkData !== undefined) {
                          if (decorationIds.some((d) => d === oldDecorations[0].id)) {
                            const newIdList = decorationIds.filter(
                              (v) => v !== oldDecorations[0].id,
                            );
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
                            this.setState({
                              decorationIds: newIdList.splice(
                                newIdList.length + 1,
                                0,
                                newDecoration.toString(),
                              ),
                            });
                            this.handlefeedbackList(
                              newDecoration,
                              title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                              filename,
                              'add',
                              selectionRange,
                              hunkData,
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
                            this.setState({
                              decorationIds: decorationIds.splice(
                                decorationIds.length + 1,
                                0,
                                newDecoration.toString(),
                              ),
                            });
                            this.handlefeedbackList(
                              newDecoration,
                              title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                              filename,
                              'add',
                              selectionRange,
                              hunkData,
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
                        const hunkData = diffEditChanges.find((resp) => {
                          if (
                            (selectionRange.startLineNumber <= resp.beginB &&
                              selectionRange.endLineNumber >= resp.beginB) ||
                            (selectionRange.startLineNumber >= resp.beginB &&
                              selectionRange.startLineNumber <= resp.endB)
                          ) {
                            return resp;
                          } else {
                            return undefined;
                          }
                        });
                        const oldDecorations = ed.getDecorationsInRange(selectionRange);
                        if (oldDecorations !== null && hunkData !== undefined) {
                          if (decorationIds.some((d) => d === oldDecorations[0].id)) {
                            const newIdList = decorationIds.filter(
                              (v) => v !== oldDecorations[0].id,
                            );
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
                            this.setState({
                              decorationIds: newIdList.splice(
                                newIdList.length + 1,
                                0,
                                newDecoration.toString(),
                              ),
                            });
                            this.handlefeedbackList(
                              newDecoration,
                              title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                              filename,
                              'reject',
                              selectionRange,
                              hunkData,
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
                            this.setState({
                              decorationIds: decorationIds.splice(
                                decorationIds.length + 1,
                                0,
                                newDecoration.toString(),
                              ),
                            });
                            this.handlefeedbackList(
                              newDecoration,
                              title === 'Bug Inducing Commit' ? 'bic' : 'bfc',
                              filename,
                              'reject',
                              selectionRange,
                              hunkData,
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
                    label: 'feedback: confirm',
                    keybindingContext: undefined,
                    contextMenuGroupId: 'navigation',
                    contextMenuOrder: 3,
                    run: (ed) => {
                      const selectionRange = ed.getSelection();
                      if (selectionRange) {
                        const hunkData = diffEditChanges.find((resp) => {
                          if (
                            (selectionRange.startLineNumber <= resp.beginB &&
                              selectionRange.endLineNumber >= resp.beginB) ||
                            (selectionRange.startLineNumber >= resp.beginB &&
                              selectionRange.startLineNumber <= resp.endB)
                          ) {
                            return resp;
                          } else {
                            return undefined;
                          }
                        });
                        const oldDecorations = ed.getDecorationsInRange(selectionRange);
                        if (oldDecorations !== null && hunkData !== undefined) {
                          if (decorationIds.some((d) => d === oldDecorations[0].id)) {
                            const newIdList = decorationIds.filter(
                              (v) => v !== oldDecorations[0].id,
                            );
                            const newDecoration = ed.deltaDecorations(
                              [oldDecorations[0].id],
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
                            this.setState({
                              decorationIds: newIdList.splice(
                                newIdList.length + 1,
                                0,
                                newDecoration.toString(),
                              ),
                            });
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
                            this.setState({
                              decorationIds: decorationIds.splice(
                                decorationIds.length + 1,
                                0,
                                newDecoration.toString(),
                              ),
                            });
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
                <div className="header flex between none" onClick={this.handleShowConsole}>
                  <div className="title" style={{ fontSize: '16px', fontWeight: 'bolder' }}>
                    Console {version === 'firstOp' ? oldVersionText : newVersionText}
                  </div>
                  <div className="tools">
                    <Button minimal icon={showConsole ? 'chevron-down' : 'chevron-up'} />
                  </div>
                </div>
                <div id="logsFlow" className="Logs">
                  {logs}
                </div>
              </section>
            </div>
          </div>
        </ResizeSensor>
        <Modal
          width="80%"
          visible={showCodeDetails}
          onCancel={() => this.setState({ showCodeDetails: false })}
          footer={null}
        >
          <CodeDetails
            regressionUuid={regressionUuid}
            diffEditDetails={diffEditChanges}
            revisionFlag={title}
            criticalChangeOriginal={original}
            criticalChangeNew={modifiedNewCode}
            fileName={filename}
          />
        </Modal>
      </>
    );
  }
}

export default CodeEditor;
