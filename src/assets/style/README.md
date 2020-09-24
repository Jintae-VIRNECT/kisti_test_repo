## VIRNECT Remote 스타일 가이드
### 스펙 
 
스타일시트는 CSS 전처리기 사용을 전제로 작성되었으며, 
이 문서의 샘플코드는 [SCSS](https://sass-lang.com/)를 사용하여 작성되었습니다. 
최신 크롬 브라우저를 지원합니다. `문서작성일 기준: Chrome 73.0.3683.103(공식 빌드) (64비트)`
 
### 폴더구조

```
/src/assets/style/
|-- workspace/
|-- workspace.scss
|-- service/
|-- service.scss
|-- support.scss
|-- plugin/
    |-- vue-toasted.scss
    `-- material-icons.scss
|-- _vars.scss
|-- _mixin.scss
|-- _font.scss
|-- _reset.scss
|-- common.scss
`-- policy.scss

```

#### common.scss 
프로젝트 전체 페이지에서 사용되는 Stylesheet들을 불러오는 파일. 
항상 전역(최상위 컴포넌트)에 들어가게 됩니다. 
 
 
#### _font.scss 
프로젝트에서 사용되는 폰트를 정의한 파일. 

 
#### _mixin.scss 
믹스인 변수명 선언 규칙은 @[속성]\_[디바이스]_[식별값] 형태로 작성합니다. 
 
 
#### _reset.less 
브라우저 간 공통된 스타일을 위한 스타일시트 초기화 파일입니다. 

### plugin/
프로젝트에서 사용되는 외부 플러그인 스타일시트를 포함한 디렉토리.  

``` 

#### 해상도 대응

Desktop 최대 넓이: 1920px\
Desktop 최소 넓이: 1200px\
Desktop 여백: 25px
 
(1199px~1025px 영역은 가로 스크롤 존재. (1200px 기준) )
 
Pad 최대 넓이: 1024px;\
Pad 최소 넓이: 768px;
 
Mobile 최대 넓이: 767px;\
Mobile 최소 넓이: 320px;\
Mobile 여백: 20px;