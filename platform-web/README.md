## ENV
node.js. yarn
## Quick Start

We suggest use Yarn to manage the the project.
1. Install dependencies (when first start)
```
yarn install
```
2. Start the web UI
```
yarn start
```

## Try in Other Way

1. Install dependencies

```
npm install
```

> Except the first and the last step, if any error or warning is reported, you may try to install these followings dependencies to solve the problem. Otherwise, ignore it.

2. In order to install 'react-monaco-editor' and 'monaco-editor-webpack-plugin'

```
npm add react-monaco-editor
npm install monaco-editor-webpack-plugin
```

if monaco continues to report error, re-install 'monaco-editor'

```
npm install monaco-editor
```

3. In order to install 'core-js' or 'core-js@3.18.3'

```
npm install core-js
npm install --save core-js@3.18.3
```

4. In order to install 'blueprintjs'

```
npm install --save @blueprintjs/core
```

5. In order to let Typescript automatic generated 'uuid'

```
npm install --save @types/uuid
```

6. '@umi/' Installation tutorial: https://www.cnblogs.com/zhaoxxnbsp/p/12672652.html#2%E5%AE%89%E8%A3%85

7. When the terminal shows compile errors, you can refer to following installs

```
npm i @ant-design/pro-card@1.18.0 --save
npm i @ant-design/pro-form@1.50.0 --save
npm i @ant-design/pro-table@2.61.0 --save
```

8. When `export 'ReactReduxContext' (imported as 'ReactReduxContext') was not found in 'react-redux' ` Warning appears

```
npm install react-redux@7.2.4
```

## Q & A

### 1. How to mock ?

The 'mock' at here means the request send by the model to the backend and return the simulated data. After 'npm start', mock will be enable by default, just refer to the official examples in the '/mock' folder to learn more. In other words, as long as the interface path in the mock is consistent with that in the backend, the mock method will be taken first, if there is no interface in the mock, then the backend interface will be requested.

## TODO

1. Try to overpass the login function at the front-end by mocking data. --rich
