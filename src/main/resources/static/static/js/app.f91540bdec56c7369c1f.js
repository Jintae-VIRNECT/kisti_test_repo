webpackJsonp([1],{"08BO":function(e,t){},NHnr:function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s=r("7+uW"),a={render:function(){var e=this.$createElement,t=this._self._c||e;return t("div",{attrs:{id:"app"}},[t("div",{staticClass:"container"},[t("router-view")],1)])},staticRenderFns:[]},i=r("VU/8")({computed:{currentUser:function(){return this.$store.state.user}},methods:{logOut:function(){this.$store.dispatch("auth/logout"),this.$route.push("/")}}},a,!1,null,null,null).exports,n=r("/ocq"),o=r("Zrlr"),l=r.n(o),u=function e(t,r,s){l()(this,e),this.email=t,this.password=r,this.rememberMe=s},m={name:"login",computed:{loggedIn:function(){return this.$store.state.auth.status.loggedIn}},data:function(){return{login:new u("smic1","smic1234"),loading:!1,message:""}},mounted:function(){this.loggedIn&&this.$router.push("/profile")},methods:{handleLogin:function(){var e=this;this.loading=!0,this.$validator.validateAll(),this.errors.any()?this.loading=!1:this.login.email&&this.login.password&&this.$store.dispatch("auth/login",this.login).then(function(){e.$router.push("/profile")},function(t){e.loading=!1,e.message=t.message})}}},c={render:function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("div",{staticClass:"col-md-12"},[r("div",{staticClass:"card card-container"},[r("img",{staticClass:"profile-img-card",attrs:{id:"profile-img",src:"//ssl.gstatic.com/accounts/ui/avatar_2x.png"}}),e._v(" "),r("form",{attrs:{name:"form"},on:{submit:function(t){return t.preventDefault(),e.handleLogin(t)}}},[r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"email"}},[e._v("이메일")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.login.email,expression:"login.email"},{name:"validate",rawName:"v-validate",value:"required",expression:"'required'"}],staticClass:"form-control",attrs:{id:"email",type:"text",name:"email"},domProps:{value:e.login.email},on:{input:function(t){t.target.composing||e.$set(e.login,"email",t.target.value)}}}),e._v(" "),e.errors.has("email")?r("div",{staticClass:"alert alert-danger",attrs:{role:"alert"}},[e._v("이메일을 입력해주세요\n        ")]):e._e()]),e._v(" "),r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"password"}},[e._v("비밀번호")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.login.password,expression:"login.password"},{name:"validate",rawName:"v-validate",value:"required",expression:"'required'"}],staticClass:"form-control",attrs:{id:"password",type:"password",name:"password"},domProps:{value:e.login.password},on:{input:function(t){t.target.composing||e.$set(e.login,"password",t.target.value)}}}),e._v(" "),e.errors.has("password")?r("div",{staticClass:"alert alert-danger",attrs:{role:"alert"}},[e._v("비밀번호를 입력해주세요\n        ")]):e._e()]),e._v(" "),r("div",{staticClass:"form-group"},[r("button",{staticClass:"btn btn-primary btn-block",attrs:{disabled:e.loading}},[r("span",{directives:[{name:"show",rawName:"v-show",value:e.loading,expression:"loading"}],staticClass:"spinner-border spinner-border-sm"}),e._v(" "),r("span",[e._v("로그인")])])]),e._v(" "),r("div",{staticClass:"form-group"},[r("div",{staticClass:"custom-control custom-checkbox"},[r("input",{directives:[{name:"model",rawName:"v-model",value:e.login.rememberMe,expression:"login.rememberMe"}],staticClass:"custom-control-input",attrs:{id:"remember-me",type:"checkbox",name:"rememberMe"},domProps:{checked:Array.isArray(e.login.rememberMe)?e._i(e.login.rememberMe,null)>-1:e.login.rememberMe},on:{change:function(t){var r=e.login.rememberMe,s=t.target,a=!!s.checked;if(Array.isArray(r)){var i=e._i(r,null);s.checked?i<0&&e.$set(e.login,"rememberMe",r.concat([null])):i>-1&&e.$set(e.login,"rememberMe",r.slice(0,i).concat(r.slice(i+1)))}else e.$set(e.login,"rememberMe",a)}}}),e._v(" "),r("label",{staticClass:"custom-control-label",attrs:{for:"remember-me"}},[e._v("자동 로그인")])])]),e._v(" "),r("div",{staticClass:"form-group"},[e.message?r("div",{staticClass:"alert alert-danger",attrs:{role:"alert"}},[e._v(e._s(e.message))]):e._e()])]),e._v(" "),e._m(0)])])},staticRenderFns:[function(){var e=this.$createElement,t=this._self._c||e;return t("div",{staticClass:"mt-4"},[t("div",{staticClass:"d-flex justify-content-center links"},[t("a",{staticClass:"ml-2",attrs:{href:"/register"}},[this._v("회원가입")])]),this._v(" "),t("div",{staticClass:"d-flex justify-content-center links"},[t("a",{attrs:{href:"/find"}},[this._v("비밀번호 찾기")])])])}]};var d=r("VU/8")(m,c,!1,function(e){r("p+jK")},"data-v-4dcd3cc0",null).exports,v=r("//Fk"),g=r.n(v),f=function e(t,r,s,a,i,n,o,u,m,c){l()(this,e),this.name=t,this.email=r,this.password=s,this.profile=a,this.phoneNumber=i,this.recoveryEmail=n,this.birth=o,this.registerInfo=u,this.serviceInfo=m,this.session=c},p=r("mvHQ"),h=r.n(p),_=r("wxAW"),b=r.n(_),w=r("mtWM"),C=r.n(w),y={auth:{login:"/auth/signin",register:"/auth/signup",emailAuth:"/auth/email"},user:{login:"/users/login",register:"/users/register",emailAuth:"/users/email"}},x=(r("xYhK"),C.a.create({timeout:1e4,headers:{"Content-Type":"application/json"}})),I=new(function(){function e(){l()(this,e)}return b()(e,[{key:"login",value:function(e){return x.post("http://localhost:8073"+y.auth.login,{email:e.email,password:e.password}).then(this.handleResponse).then(function(e){var t=e.data;return console.log(t),t.accessToken&&localStorage.setItem("user",h()(t)),t})}},{key:"logout",value:function(){localStorage.removeItem("user")}},{key:"register",value:function(e){return x.post("http://localhost:8073"+y.auth.register,{username:e.username,email:e.email,password:e.password})}},{key:"emailAuth",value:function(e){return x.post("http://localhost:8073"+y.auth.emailAuth,{email:e}).then(this.handleResponse).then(function(e){return e.data.data})}},{key:"handleResponse",value:function(e){var t=e.data;if(200!==e.status||200!==t.code){localStorage.removeItem("user"),location.reload(!0);var r=t.message;return g.a.reject(r)}return g.a.resolve(t)}}]),e}()),N={name:"register",computed:{loggedIn:function(){return this.$store.state.auth.status.loggedIn}},data:function(){return{register:new f("","","","","","","","","",""),submitted:!1,successful:!1,isSendEmail:!1,isValidEmail:!1,isShow:!1,verificationCode:0,message:"",form:{image:""}}},mounted:function(){this.loggedIn&&this.$router.push("/")},methods:{handleRegister:function(){var e=this;this.message="",this.submitted=!0,console.log(this.register),this.$validator.validate().then(function(t){t&&e.$store.dispatch("auth/register",e.register).then(function(t){e.message=t.message,e.successful=!0},function(t){e.message=t.message,e.successful=!1}).then(e.$router.push("/"))})},validImage:function(e){var t=this,r=e.target.files;return console.log(r),new g.a(function(e,s){if(r.length>0){if(["image/gif","image/jpeg","image/jpg","image/png"].indexOf(r[0].type)<0)return void s("This image is unavailable.");if(r[0].size>2097152)return void s("This image size is unavailable.");t.form.image=null;var a=new FileReader;a.onload=function(t){var r=t.target.result,a=new Image;a.onload=function(t){e(r),t.target.remove()},a.onerror=function(e){s("This image is unavailable.")},a.src=r},a.readAsDataURL(r[0])}})},uploadImage:function(e){var t=this,r=e.target.files;this.validImage(e).then(function(e){console.log(e),t.register.profile=r[0],t.register.profile=e,t.form.image=e}).catch(function(e){console.log(e)})},sendEmail:function(){alert(this.register.email);var e=this.register.email;console.log(e);I.emailAuth(e);console.log(e),this.isShow=!0},checkVerificationCode:function(){}}},$={render:function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("div",{staticClass:"row"},[r("div",{staticClass:"card card-container"},[r("form",{attrs:{id:"form",name:"form"},on:{submit:function(t){return t.preventDefault(),e.handleRegister(t)}}},[e.successful?e._e():r("div",[r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"username"}},[e._v("이름")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.register.name,expression:"register.name"},{name:"validate",rawName:"v-validate",value:"required|min:3|max:20",expression:"'required|min:3|max:20'"}],staticClass:"form-control",attrs:{id:"username",type:"text",name:"name"},domProps:{value:e.register.name},on:{input:function(t){t.target.composing||e.$set(e.register,"name",t.target.value)}}}),e._v(" "),e.submitted&&e.errors.has("username")?r("div",{staticClass:"alert-danger"},[e._v(e._s(e.errors.first("username"))+"\n          ")]):e._e()]),e._v(" "),r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"email"}},[e._v("이메일")]),e._v(" "),r("div",{staticClass:"form-inline"},[r("input",{directives:[{name:"model",rawName:"v-model",value:e.register.email,expression:"register.email"},{name:"validate",rawName:"v-validate",value:"required|email|max:50",expression:"'required|email|max:50'"}],staticClass:"form-control col-sm-8",attrs:{id:"email",type:"email",name:"email"},domProps:{value:e.register.email},on:{input:function(t){t.target.composing||e.$set(e.register,"email",t.target.value)}}}),e._v(" "),r("b-button",{directives:[{name:"b-modal",rawName:"v-b-modal.email-verify",modifiers:{"email-verify":!0}}],staticClass:"form-control col-sm-4"},[e._v("인증")])],1),e._v(" "),e.submitted&&e.errors.has("email")?r("div",{staticClass:"alert-danger"},[e._v(e._s(e.errors.first("email"))+"\n          ")]):e._e()]),e._v(" "),r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"password"}},[e._v("비밀번호")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.register.password,expression:"register.password"},{name:"validate",rawName:"v-validate",value:"required|min:6|max:40",expression:"'required|min:6|max:40'"}],staticClass:"form-control",attrs:{id:"password",type:"password",name:"password"},domProps:{value:e.register.password},on:{input:function(t){t.target.composing||e.$set(e.register,"password",t.target.value)}}}),e._v(" "),e.submitted&&e.errors.has("password")?r("div",{staticClass:"alert-danger"},[e._v(e._s(e.errors.first("password"))+"\n          ")]):e._e()]),e._v(" "),r("div",{staticClass:"form-group"},[e.form.image?r("img",{staticClass:"profile-img-card",attrs:{id:"thumbnail",src:e.register.profile}}):e._e(),e._v(" "),r("div",{staticClass:"form-inline"},[r("label",{staticClass:"fom",attrs:{for:"profile"}},[e._v("프로필")]),e._v(" "),r("input",{staticClass:"form-control-file",attrs:{id:"profile",type:"file",name:"profile",accept:"image/gif,image/jpeg,image/png"},on:{change:function(t){return e.uploadImage(t)}}})])]),e._v(" "),r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"phoneNumber"}},[e._v("전화번호")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.register.phoneNumber,expression:"register.phoneNumber"}],staticClass:"form-control",attrs:{id:"phoneNumber",type:"tel",name:"phoneNumber"},domProps:{value:e.register.phoneNumber},on:{input:function(t){t.target.composing||e.$set(e.register,"phoneNumber",t.target.value)}}})]),e._v(" "),r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"recoveryEmail"}},[e._v("복구 이메일")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.register.recoveryEmail,expression:"register.recoveryEmail"}],staticClass:"form-control",attrs:{id:"recoveryEmail",type:"email",name:"recoveryEmail"},domProps:{value:e.register.recoveryEmail},on:{input:function(t){t.target.composing||e.$set(e.register,"recoveryEmail",t.target.value)}}})]),e._v(" "),r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"birth"}},[e._v("생년월일")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.register.birth,expression:"register.birth"}],staticClass:"form-control",attrs:{id:"birth",type:"text",name:"birth"},domProps:{value:e.register.birth},on:{input:function(t){t.target.composing||e.$set(e.register,"birth",t.target.value)}}})]),e._v(" "),r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"registerInfo"}},[e._v("가입 경로")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.register.registerInfo,expression:"register.registerInfo"},{name:"validate",rawName:"v-validate",value:"required",expression:"'required'"}],staticClass:"form-control",attrs:{id:"registerInfo",type:"text",name:"registerInfo"},domProps:{value:e.register.registerInfo},on:{input:function(t){t.target.composing||e.$set(e.register,"registerInfo",t.target.value)}}}),e._v(" "),e.submitted&&e.errors.has("registerInfo")?r("div",{staticClass:"alert-danger"},[e._v(e._s(e.errors.first("registerInfo"))+"\n          ")]):e._e()]),e._v(" "),r("div",{staticClass:"form-group"},[r("label",{attrs:{for:"serviceInfo"}},[e._v("서비스 분야")]),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.register.serviceInfo,expression:"register.serviceInfo"},{name:"validate",rawName:"v-validate",value:"required",expression:"'required'"}],staticClass:"form-control",attrs:{id:"serviceInfo",type:"text",name:"serviceInfo"},domProps:{value:e.register.serviceInfo},on:{input:function(t){t.target.composing||e.$set(e.register,"serviceInfo",t.target.value)}}}),e._v(" "),e.submitted&&e.errors.has("serviceInfo")?r("div",{staticClass:"alert-danger"},[e._v(e._s(e.errors.first("serviceInfo"))+"\n          ")]):e._e()]),e._v(" "),e._m(0)])]),e._v(" "),e.message?r("div",{staticClass:"alert",class:e.successful?"alert-success":"alert-danger"},[e._v(e._s(e.message)+"\n    ")]):e._e()]),e._v(" "),r("div",[r("b-modal",{attrs:{id:"email-verify",centered:"",title:"이메일 인증"}},[r("div",[r("b-form-input",{attrs:{placeholder:"이메일로 전송된 인증코드 6자리를 입력하세요"},model:{value:e.verificationCode,callback:function(t){e.verificationCode=t},expression:"verificationCode"}}),e._v(" "),r("div",{staticClass:"mt-2"},[e._v("이메일로 전송된 인증코드 6자리를 입력하세요")])],1)])],1)])},staticRenderFns:[function(){var e=this.$createElement,t=this._self._c||e;return t("div",{staticClass:"form-group"},[t("button",{staticClass:"btn btn-primary btn-block",attrs:{type:"submit"}},[this._v("회원가입")]),this._v(" "),t("div",{staticClass:"d-flex justify-content-center links"},[t("a",{staticClass:"ml-2",attrs:{href:"/"}},[this._v("로그인")])])])}]};var k=r("VU/8")(N,$,!1,function(e){r("08BO")},"data-v-fa96a866",null).exports,q={name:"profile",data:function(){return{content:""}},mounted:function(){this.content=JSON.parse(localStorage.getItem("user"))},methods:{pretty:function(e){return h()(JSON.parse(e),null,2)}}},E={render:function(){var e=this.$createElement,t=this._self._c||e;return t("div",{staticClass:"container"},[this._m(0),this._v(" "),t("div",[t("h5",[t("pre",[this._v(this._s(this._f("pretty")(this.content)))])])])])},staticRenderFns:[function(){var e=this.$createElement,t=this._self._c||e;return t("header",{staticClass:"jumbotron"},[t("h3",[t("p",[this._v("User Info")])])])}]},S=r("VU/8")(q,E,!1,null,null,null).exports;s.default.use(n.a);var j=new n.a({mode:"history",routes:[{path:"/",component:d},{path:"/register",component:k},{path:"/profile",name:"profile",component:S}]}),M=r("NYxO"),A=JSON.parse(localStorage.getItem("login")),P={namespaced:!0,state:A?{status:{loggedIn:!0},user:A}:{status:{},user:null},actions:{login:function(e,t){var r=e.commit;return I.login(t).then(function(e){return r("loginSuccess",e),g.a.resolve(e)},function(e){return r("loginFailure"),g.a.reject(e)})},logout:function(e){var t=e.commit;I.logout(),t("logout")},register:function(e,t){var r=e.commit;return I.register(t).then(function(e){return r("registerSuccess"),g.a.resolve(e.data)},function(e){return r("registerFailure"),g.a.reject(e.response.data)})}},mutations:{loginSuccess:function(e,t){e.status={loggedIn:!0},e.user=t},loginFailure:function(e){e.status={},e.user=null},logout:function(e){e.status={},e.user=null},registerSuccess:function(e){e.status={}},registerFailure:function(e){e.status={}}}};s.default.use(M.a);var F=new M.a.Store({modules:{auth:P}}),R=r("Tqaz"),O=(r("qb6w"),r("qwyH"),r("sUu7")),U=r("C/JF"),T=r("1e6/"),V=r("fhbW");U.c.add(V.a,V.d,V.e,V.b,V.c),s.default.config.productionTip=!1,s.default.use(R.a),s.default.use(O.a),s.default.component("font-awesome-icon",T.a),new s.default({router:j,store:F,render:function(e){return e(i)}}).$mount("#app")},"p+jK":function(e,t){},qb6w:function(e,t){},qwyH:function(e,t){}},["NHnr"]);
//# sourceMappingURL=app.f91540bdec56c7369c1f.js.map