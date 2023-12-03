import {DiffEditDetailItems} from '@/pages/editor/data';
import ProDescriptions from '@ant-design/pro-descriptions';
import {MonacoDiffEditor} from 'react-monaco-editor';

interface IProps {
  regressionUuid: string;
  revisionFlag: string; // Bug Inducing Commit || Bug Fixing Commit
  criticalChangeOriginal?: string;
  criticalChangeNew?: string;
  diffEditDetails?: DiffEditDetailItems[];
  fileName: string; // index
  codeRange?: number[];
  endLine?: number[];
}

// interface SearchParams {
//   feedback: string;
// }

// const generateParams = (params: SearchParams) => {
//   return {
//     feedback: params.feedback,
//   };
// };

const mockData = [
  {
    uuid: '64bcef94-d8ee-46c9-a82b-393ef6a1d898',
    codeRange: [203, 207, 61, 62],
  },
  {
    uuid: 'cfccf309-bbbe-42d2-865b-2a50a1288113',
    codeRange: [148, 148, 271, 286],
  },
  {
    uuid: '4efd2990-bd48-418e-9636-c035abd850f5',
    codeRange: [148, 148, 254, 268],
  },
  {
    uuid: '156eb75f-0b5a-4a35-bd59-db0a57ed9f0e',
    codeRange: [2059, 2070, 913, 913],
  },
  {
    uuid: 'bd2ab6c2-5681-4605-ae06-3ee3ca0ad51b',
    codeRange: [2059, 2070, 913, 913],
  },
  {
    uuid: '19c7bc2b-8cc9-4477-b155-8c3a13bab168',
    codeRange: [148, 148, 321, 327],
  },
  {
    uuid: '76ea45dc-c810-4c26-b104-54a19c041ba0',
    codeRange: [147, 173, 0, 0],
  },
  {
    uuid: '82333b1d-79cf-449b-8b06-c2b042bb56a4',
    codeRange: [124, 144, 140, 140],
  },
  {
    uuid: '7606319e-1f8e-467d-b3fc-f51331f8c0a4',
    codeRange: [365, 375, 365, 365],
  },
  {
    uuid: 'f5d0e242-30be-42c6-a852-082c958f0907',
    codeRange: [181, 183, 182, 182],
  },
];

const CodeDetails: React.FC<IProps> = ({
                                         regressionUuid,
                                         revisionFlag,
                                         criticalChangeOriginal,
                                         criticalChangeNew,
                                         diffEditDetails,
                                         fileName,
                                       }) => {
  const target = mockData.find((d) => {
    return d.uuid === regressionUuid;
  });

  // useEffect(() => {

  // }, []);

  return (
    <>
      {target !== undefined ? (
        <ProDescriptions column={2} title={fileName + ' Critical Change Details'}>
          <ProDescriptions.Item label="Regression Uuid">{regressionUuid}</ProDescriptions.Item>
          <ProDescriptions.Item label="Revision Flag">{revisionFlag}</ProDescriptions.Item>
          {revisionFlag === 'Bug Inducing Commit' ? (
            <ProDescriptions.Item span={2} label="Critical change Line Range">
              {target.codeRange[0]} ~ {target.codeRange[1]}
            </ProDescriptions.Item>
          ) : (
            <ProDescriptions.Item span={2} label="Code Line Range">
              {target.codeRange[2]} ~ {target.codeRange[3]}
            </ProDescriptions.Item>
          )}
          <ProDescriptions.Item
            span={2}
            label="Critical Change"
            valueType="code"
            style={{width: 1000, height: 400}}
          >
            <MonacoDiffEditor
              width={800}
              height={400}
              language={'java'}
              options={{
                renderSideBySide: false,
                originalEditable: false,
                fontSize: 14,
                lineHeight: 18,
                folding: false,
                scrollbar: {
                  verticalScrollbarSize: 0,
                  verticalSliderSize: 14,
                  horizontalScrollbarSize: 0,
                  horizontalSliderSize: 14,
                  alwaysConsumeMouseWheel: false,
                },
                renderIndicators: false,
              }}
              original={criticalChangeOriginal}
              value={criticalChangeNew}
              editorDidMount={(diffEditor) => {
                if (revisionFlag === 'Bug Inducing Commit') {
                  diffEditor.revealLineInCenter(target.codeRange[0]);
                } else {
                  diffEditor.revealLineInCenter(target.codeRange[2]);
                }
                // monaco.editor.colorizeModelLine(monaco.editor.getModels()[0], 2);
              }}
            />
          </ProDescriptions.Item>
          {/* <ProDescriptions.Item span={2} label="feedback">
            <Input.Group compact>
              <Input.TextArea
                rows={3}
                showCount
                allowClear
                maxLength={200}
                style={{ width: 'calc(100% - 200px)' }}
                onChange={() => setOnSubmit(false)}
              />
              <Button
                type="primary"
                style={{ display: 'flex' }}
                onClick={() => {
                  message.success('Feedback submited, thanks for your contribution!');
                  onCancel(false);
                }}
                disabled={onSubmit}
              >
                Submit
              </Button>
            </Input.Group>
          </ProDescriptions.Item> */}
        </ProDescriptions>
      ) : (
        <div>{diffEditDetails}</div>
      )}
    </>
  );
};

export default CodeDetails;
