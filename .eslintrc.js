const { prettierRules } = require('@virnect/eslint-config/base')

module.exports = {
  extends: ['@virnect/eslint-config/vue'],
  env: {
    node: true,
    jest: true,
  },
  plugins: ['jest', 'vue'],
  rules: {
    "prettier/prettier": ['error', {
      ...prettierRules,
      vueIndentScriptAndStyle: false,
    }],
  },
}