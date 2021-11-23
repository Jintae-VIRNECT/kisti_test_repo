const { prettierRules } = require('@virnect/eslint-config/base')

module.exports = {
  "parserOptions": {
    "parser": "babel-eslint",
    "ecmaVersion": 2018,
    "sourceType": "module",
  },
  extends: ["@virnect/eslint-config/vue"],
  env: {
    node: true,
    es6: true,
    "jest": true
  },
  rules: {
    "prettier/prettier": ['error', {
      ...prettierRules,
      vueIndentScriptAndStyle: false,
    }],
  },
}