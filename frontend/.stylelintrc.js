module.exports = {
  plugins: [
    "stylelint-prettier", 
    "stylelint-scss", 
    "stylelint-order", 
    "stylelint-config-rational-order/plugin",
  ],
  extends: [
    "stylelint-prettier/recommended", 
    "stylelint-config-rational-order",
  ],
  rules: {
    "prettier/prettier": [true, {"tabWidth": 4}],
    "order/properties-order": [],
    "plugin/rational-order": [true, {}]
  }
};