<template>
  <div class="container">
    <header class="jumbotron">
      <h3>
        <p>User Info</p>
      </h3>
    </header>
    <div>
      <h5>
        User Detail Info API Result
        <pre>{{ userInfo }}</pre>
      </h5>
    </div>
    <div>
      <h5>
        User Login API Result
        <pre>{{ content | pretty }}</pre>
      </h5>
    </div>
    <div>
      <button @click="main">메인으로</button>
      <button @click="logout">로그아웃</button>
    </div>
  </div>
</template>

<script>
  import UserService from 'service/user-service';
  import AuthService from 'service/auth-service';

  export default {
    name: 'profile',
    data() {
      return {
        content: '',
        userInfo: '',
        userUUID: '',
        accessToken: '',
      }
    },
    mounted() {
      UserService.getUserContent().then(
        response => {
          if(response.data.code === 200){
            const userInfo = JSON.parse(JSON.stringify(response.data.data));
            this.userUUID = userInfo.userInfo.uuid;
          }
          this.userInfo = JSON.stringify(response.data, null, 2);
        },
        error => {
          this.userInfo = error.response.data.message;
        }
      );
      this.content = JSON.parse(localStorage.getItem('user'));
      this.accessToken = this.content.accessToken;
    },
    methods: {
      pretty(value) {
        return JSON.stringify(JSON.parse(value), null, 2);
      },
      main() {
        localStorage.removeItem('user')
        location.href = '/';
      },
      logout() {
        // alert("hi!");
        // alert(`uuid: ${this.userUUID} , accessToken: ${this.accessToken}`);
        AuthService.logout({uuid: this.userUUID, accessToken: this.accessToken}).then(
          response => {
            // alert(response);
            console.log(response)
            this.main();
          },
          error =>{
            this.userInfo = error.response.data.message;
            alert(this.userInfo);
          }
        )
      }
    }
  };
</script>
