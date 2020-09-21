# Current release - 11.09.2020

### AWS S3 Bucket
* Added and linked bucket for remote room profile image upload

### File Rest API Update
* GET /remote/file **Load Remote Session File List**
* DELETE /remote/file/{workspaceId}/{sessionId} **Delete the specific fileDelete a Specific file**
* POST /remote/file/{workspaceId}/{sessionId}/profile **Update a Remote Session profile image file**
* GET /remote/file/download/{workspaceId}/{sessionId} **Download a Specific file**
* GET /remote/file/download/url/{workspaceId}/{sessionId} **Get URL to downloadGet URL to download**
* POST /remote/file/upload **Upload a file to Storage Server**

###  Bug Fixes
* Improvement in Invite a member to a specific remote session
* Improvement in force disconnect member from a specific remote session
* Send Push message using message server
* Improvement in Invite a member when re-invite member after member is kicked out 

# Next release
* API DELETE /remote/room/{workspaceId}/{sessionId}/member **(Deprecated)**
* API GET /remote/file/download/{workspaceId}/{sessionId} **(Deprecated)**
* API for user account exit scheduled to be added.
* Add and link bucket for remote room file share.


# Medium term
* On premises installation license management
* Kurento media server discovery 

# Long term
* ... 
