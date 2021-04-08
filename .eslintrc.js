module.exports = {
  extends: ['@virnect/eslint-config/vue'],
  globals: {
    // cypress
    Cypress: 'readonly',
    describe: 'readonly',
    it: 'readonly',
    cy: 'readonly',
    beforeEach: 'readonly',
  },
}
