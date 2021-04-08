describe('before login', () => {
  it('헬스체크', () => {
    cy.visit('/healthcheck')
    cy.get('body').contains('200')
  })
  it('로그인하지 않았을 경우 리다이렉트', () => {
    cy.intercept('/').as('main')
    cy.visit('/')
    cy.wait('@main').its('response.statusCode').should('eq', 302)
  })
})

describe('after login', () => {
  // 테스트 계정
  const user = {
    email: 'test5@test.com',
    password: 123456,
  }
  // 로그인
  before(() => {
    cy.request({
      method: 'POST',
      url: 'https://192.168.6.3:8073/auth/signin',
      body: {
        email: user.email,
        password: user.password,
      },
    }).then(({ body }) => {
      cy.setCookie('accessToken', body.data.accessToken)
      cy.setCookie('refreshToken', body.data.refreshToken)
    })
    cy.visit('/')
  })

  it('메인화면 표시', () => {
    cy.title().should('match', /(Workstation|워크스테이션)/)
  })

  it('로그인 정보 표시', () => {
    cy.get('header .thumbnail-btn').click()
    cy.get('header .email-text').should('have.text', user.email)
  })

  it('페이지 순회', () => {
    cy.get('.the-sidebar__logo').click()
    // members
    cy.get('.the-sidebar__menus__item > a[href="/members"]').click()
    cy.get('.the-sidebar__collapse__body > a[href="/members"]').click()
    cy.get('#members').should('be.visible')
    cy.get('.the-sidebar__menus__item > a[href="/members"]').click()
    cy.get('.the-sidebar__collapse__body > a[href="/members/activity"]').click()
    cy.get('#members-activity').should('be.visible')
    // contents
    cy.get('.the-sidebar__menus__item > a[href="/contents"]').click()
    cy.get('.the-sidebar__collapse__body > a[href="/contents"]').click()
    cy.get('#contents').should('be.visible')
    // tasks
    cy.get('.the-sidebar__menus__item > a[href="/tasks"]').click()
    cy.get('.the-sidebar__collapse__body > a[href="/tasks"]').click()
    cy.get('#tasks').should('be.visible')
    cy.get('.the-sidebar__menus__item > a[href="/tasks"]').click()
    cy.get('.the-sidebar__collapse__body > a[href="/tasks/results"]').click()
    cy.get('#results').should('be.visible')
    cy.get('.the-sidebar__menus__item > a[href="/tasks"]').click()
    cy.get('.the-sidebar__collapse__body > a[href="/tasks/troubles"]').click()
    cy.get('#tm').should('be.visible')
    // workspace
    cy.get('.the-sidebar__menus__item > a[href="/workspace/setting"]').click()
    cy.get('#workspace-setting').should('be.visible')
  })

  it('에러페이지', () => {
    cy.visit('/1234', { failOnStatusCode: false })
    cy.get('.virnect-error-page').should('be.visible')
  })
})
