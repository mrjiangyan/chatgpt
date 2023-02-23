module.exports = {
  root: true,
  env: {
    browser: true,
    node: true
  },
  parser: "vue-eslint-parser",
  extends: [
    'eslint:recommended',
    'plugin:vue/essential',
    'plugin:@typescript-eslint/recommended',
   
  ],
  parserOptions: {
    ecmaVersion: 2020,
    parser: '@typescript-eslint/parser',
    sourceType: 'module'
  },
  plugins: ['vue', '@typescript-eslint'],
  rules: {
    // "no-extra-semi": 2, //禁止多余的冒号
    // "generator-star-spacing": "off",
    semi: 'off',
    "@typescript-eslint/explicit-function-return-type" : "off",
    "@typescript-eslint/explicit-module-boundary-types" : "off",
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off'
    // "prettier/prettier": [
    //   "warn",
    //   {
    //     singleQuote: true
    //   }
    // ]
  }
}
