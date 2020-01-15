import Axios from 'axios'
import secureStorage from 'utils/auth'

const BASE_URL = 'http://192.168.0.121:6060';
const account = secureStorage.getItem('account');

let TOKEN = "";
if(account && account.token) {
    TOKEN = account.token;
}

const axios = Axios.create({
    baseURL: '',
    timeout: 10000,
    headers: {
        'Access-Control-Allow-Origin': '*',
        'Content-Type': 'application/json'
        // 'Content-Type': 'multipart/form-data'
    }
});

/**
 * Common request handler
 */
const sender = function(query, option) {
    const params = { query }
    return new Promise(function(resolve, reject) {
        axios.post('/api', params, option)
            .then(function(res){
                receiver(res, resolve);
            })
            .catch(function(err){
                errorHandler(err, reject);
            });
    })
}

/**
 * Common response handler
 * @param {Object} res 
 * @param {Function} callback 
 */
const receiver = function(res, callback) {
    if(res.data) {
        // const code = res.data['resCode'];
        console.log(res.data);
        // if(code === 1) {
            (typeof callback === 'function') && callback(res.data['data']);

            //Token 갱신
            if(res.data['result'] && res.data['result'].token) {
                TOKEN = res.data['result'].token;
            }
        // } else {
            // throw new Error(code);
        // }
    }
}

/**
 * Common error handler
 * @param {Object} err 
 * @param {Function} callback 
 */
const errorHandler = function(err, callback) {
    const errorList = {
        '601': 'ID or PW is not valid',
        '602': 'attempt to login as app account',
        '603': 'server login process error',
        '604': 'logout log update error',
        '605': 'invalid account level',
        '606': 'duplicate login id, this id is already used',
        '607': 'duplicate user nickname, this nickname is already used',
        '608': 'new account registration is failed',
        '609': 'Assign error nknowned team id to new account',
        '610': 'Servber User Registration Process Error',
        '611': 'Invalid Department Id Used Fro Search Teams',
        '612': 'Gathering Department List Process is Fail',
        '613': 'License Generate Error which used invalid manager id',
        '614': 'License Generate ERror which used invalid device Type',
        '615': 'License Generate until limit numbers',
        '616': 'License Generate Process Error',
        '617': 'License Allocation is Duplicated',
        '618': 'License id is invalid',
        '619': 'License Allocation Process Error',
        '620': 'License User Activate Error',
        '621': 'License User Deactivate Error',
        '622': 'License DueDate Error',
        '623': 'Account Delete Error',
        '901': 'request parameter is invalid',
        '902': 'Invalid Manager ID',
        '903': 'Invalid License Info',
        '904': 'Invalid Account ID',
        '905': 'Invalid API Token'
    }
    const error = {}
    console.log(err);
    error.code = err.message;
    error.message = errorList[error.code] || 'Undefined Error.';
    
    (typeof callback === 'function') && callback(error);
}

export default sender;