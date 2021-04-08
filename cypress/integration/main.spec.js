describe('before login', () => {
  it('헬스체크', () => {
    cy.visit('/healthcheck')
    cy.get('body').contains('200')
  })
  it('로그인하지 않을 경우 리다이렉트', () => {
    cy.intercept('/').as('main')
    cy.visit('/')
    cy.wait('@main').its('response.statusCode').should('eq', 302)
  })
})

describe('after login', () => {
  const user = {
    email: 'test5@test.com',
    password: 123456,
  }

  beforeEach(async () => {
    const { body } = await cy.request({
      method: 'POST',
      url: 'https://192.168.6.3:8073/auth/signin',
      body: {
        email: user.email,
        password: user.password,
      },
    })
    cy.setCookie('accessToken', body.data.accessToken)
    cy.setCookie('refreshToken', body.data.refreshToken)
    cy.visit('/')
  })

  it('메인화면 표시', () => {
    cy.title().should('match', /(Download|다운로드)/)
  })

  it('로그인 정보 표시', () => {
    cy.get('header .thumbnail-btn').click()
    cy.get('header .email-text').should('have.text', user.email)
  })

  it('데이터 초기 렌더링', () => {
    cy.get('#pane-remote').should('be.visible')
    cy.get('#pane-remote .el-card').should('exist')
  })

  it('탭 전환', () => {
    cy.intercept('/download/list/make').as('loadMake')
    cy.intercept('/download/list/view').as('loadView')
    cy.intercept('/download/list/track').as('loadTrack')

    cy.get('#tab-make').click()
    cy.wait('@loadMake').then(() => {
      cy.get('#pane-make').should('be.visible')
      cy.get('#pane-make .el-card').should('exist')
    })

    cy.get('#tab-view').click()
    cy.wait('@loadView').then(() => {
      cy.get('#pane-view').should('be.visible')
      cy.get('#pane-view .el-card').should('exist')
    })

    cy.get('#tab-track').click()
    cy.wait('@loadTrack').then(() => {
      cy.get('#pane-track').should('be.visible')
      cy.get('#pane-track .el-card').should('exist')
    })
  })

  it('파일 다운로드', () => {
    cy.intercept(/.*\.(zip|apk)$/).as('download')
    cy.get('.el-card .el-button--primary').first().click()
    cy.wait('@download')
      .its('response.headers.content-type')
      .should('contain', 'application')
  })
})
