

module.exports = {
  extends: ['@virnect/stylelint-config/scss'],
  overrides: [
    {
      files: ["**/*.scss"],
      customSyntax: "postcss-scss"
    }
  ]
}