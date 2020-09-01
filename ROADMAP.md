# Current release - 01.09.2020

### File Rest API Update
* GET /remote/file **Load Remote Session File List**
* DELETE /remote/file/{workspaceId}/{sessionId} **Delete the specific fileDelete a Specific file**
* POST /remote/file/{workspaceId}/{sessionId}/profile **Update a Remote Session profile image file**
* GET /remote/file/download/{workspaceId}/{sessionId} **Download a Specific file**
* GET /remote/file/download/url/{workspaceId}/{sessionId} **Get URL to downloadGet URL to download**
* POST /remote/file/upload **Upload a file to Storage Server**

###  Bug Fixs
* Improvement in Invite a member to a specific remote session
* Improvement in force disconnect member from a specific remote session
* Send Push message using message server 

# Next release
* DELETE /remote/room/{workspaceId}/{sessionId}/member **(Deprecated)**
* GET /remote/file/download/{workspaceId}/{sessionId} **(Deprecated)**
*  


# Medium term
* On premises installation license management
* Kurento media server discovery 

# Long term
* ... 
