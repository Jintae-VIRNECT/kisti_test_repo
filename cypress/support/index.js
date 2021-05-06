// ResizeObserver 버그 무시
const resizeObserverLoopErrRe = /^[^(ResizeObserver loop limit exceeded)]/
Cypress.on('uncaught:exception', err => {
  /* returning false here prevents Cypress from failing the test */
  if (resizeObserverLoopErrRe.test(err.message)) {
    return false
  }
})

// 로그인
Cypress.Commands.add('login', () => {
  cy.fixture('user.json')
    .then(user => {
      return cy.request({
        method: 'POST',
        url: 'https://192.168.6.3:8073/auth/signin',
        body: {
          email: user.email,
          password: user.password,
        },
      })
    })
    .then(({ body }) => {
      cy.setCookie('accessToken', body.data.accessToken)
      cy.setCookie('refreshToken', body.data.refreshToken)
    })
})
