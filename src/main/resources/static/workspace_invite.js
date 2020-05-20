function invite_reject(token) {
    console.log(token);
        $.ajax({
            url: "http://192.168.6.3:8082/workspaces/1/invite/accept?userId=12&code=reject",
            type: 'json',
            contentType: 'application/json; charset=UTF-8',
            data: JSON.stringify(form),
            method: 'GET',
            headers: {
                "Authorization": token
            },
            success: (response) => {
            console.log(response);
        alert("이메일 전송 완료");
    },
        error: (response) => {
            console.log(response);
            alert("이메일 전송 실패");
        }
    });

}
