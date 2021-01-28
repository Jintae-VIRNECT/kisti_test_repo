module.exports = {
  extends: ['@virnect/eslint-config/vue', 'plugin:jest/recommended'],
  rules: {
    'no-console': 'off',
  },
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: 'babel-eslint',
    sourceType: 'module',
    allowImportExportEverywhere: false,
  },
  plugins: ['jest'],
}
