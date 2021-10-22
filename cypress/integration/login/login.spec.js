/// <reference types="cypress" />

describe('example to-do app', () => {
  beforeEach(() => {
    cy.visit(Cypress.env('URL'))
  })
  it('로그인 에러테스트', () => {
    cy.get('.email-input input')
      .type(Cypress.env('ASDF'))
      .should('have.value', Cypress.env('ASDF'))
    cy.get('.password-input input')
      .type(Cypress.env('ASDF'))
      .should('have.value', Cypress.env('ASDF'))
      .type('{enter}')

    // 영문 변경
    cy.get('.language-btn').click()
    cy.get('.hover-box li:nth-child(2)').click()
    // cy.get('.email-input input')
    //   .type(Cypress.env('ID'))
    //   .should('have.value', Cypress.env('ID'))
    // cy.get('.password-input input')
    //   .type(Cypress.env('PW'))
    //   .should('have.value', Cypress.env('PW'))
  })
  it('회원가입 약관 체크', () => {
    cy.visit(Cypress.env('URL') + '/terms')
    cy.get('.all-terms').click()
    // 광고 수신 차단
    cy.get('.marketing-check').click()
    cy.get('.next-btn').click()

    // 회원가입
    cy.get('.email-input input')
      .type(Cypress.env('TEST_MAIL_ALEADY'))
      .should('have.value', Cypress.env('TEST_MAIL_ALEADY'))
    cy.get('.verification-btn').click()
    // 중복 에러
    cy.get('.email-input input')
      .clear()
      .type(Cypress.env('TEST_MAIL_DONE'))
      .should('have.value', Cypress.env('TEST_MAIL_DONE'))
    cy.get('.verification-btn').click()
  })
})
