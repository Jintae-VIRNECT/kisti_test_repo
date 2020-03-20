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
    </div>
  </div>
</template>

<script>
  import UserService from '../service/user-service';

  export default {
    name: 'profile',
    data() {
      return {
        content: '',
        userInfo: '',
      }
    },
    mounted() {
      UserService.getUserContent().then(
        response => {
          this.userInfo = JSON.stringify(response.data, null, 2);
        },
        error => {
          this.userInfo = error.response.data.message;
        }
      );
      this.content = JSON.parse(localStorage.getItem('user'));
    },
    methods: {
      pretty(value) {
        return JSON.stringify(JSON.parse(value), null, 2);
      },
      main() {
        localStorage.clear();
        location.href = '/';
      }
    }
  };
</script>
