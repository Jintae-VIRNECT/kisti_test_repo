## 회원관리 scaffolding

#### 사용법

npm registry(ex-verdaccio)에 올린 것을 import 해서 사용.

```javascript
// global use
import UserScaffold from '@virnect/scaffold-modules-user'

Vue.use(UserScaffold)
```

or

```javascript
// local use
import UserScaffold from '@virnect/scaffold-modules-user'

export default {
  components: { UserScaffold },
  ...
}
```

```HTML
<!-- html -->
<template>
  ...
  <user-scaffold></user-scaffold>
  ...
</template>
```