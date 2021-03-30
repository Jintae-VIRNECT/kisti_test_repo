import os
import sys
import requests
from dotenv import load_dotenv
from tzlocal import get_localzone
import json
import notion
from notion.client import NotionClient
from notion.block import PageBlock, TextBlock, TodoBlock, CodeBlock, HeaderBlock, CalloutBlock


# Fix Bug for notion-py
def call_load_page_chunk(self, page_id):
  
    if self._client.in_transaction():
        self._pages_to_refresh.append(page_id)
        return

    data = {
        "pageId": page_id,
        "limit": 100,
        "cursor": {"stack": []},
        "chunkNumber": 0,
        "verticalColumns": False,
    }

    recordmap = self._client.post("loadPageChunk", data).json()["recordMap"]

    self.store_recordmap(recordmap)

# Fix Bug for notion-py
def call_query_collection(
    self,
    collection_id,
    collection_view_id,
    search="",
    type="table",
    aggregate=[],
    aggregations=[],
    filter={},
    sort=[],
    calendar_by="",
    group_by="",
):

    assert not (
        aggregate and aggregations
    ), "Use only one of `aggregate` or `aggregations` (old vs new format)"

    # convert singletons into lists if needed
    if isinstance(aggregate, dict):
        aggregate = [aggregate]
    if isinstance(sort, dict):
        sort = [sort]

    data = {
        "collectionId": collection_id,
        "collectionViewId": collection_view_id,
        "loader": {
            "limit": 1000000,
            "loadContentCover": True,
            "searchQuery": search,
            "userLocale": "en",
            "userTimeZone": str(get_localzone()),
            "type": type,
        },
        "query": {
            "aggregate": aggregate,
            "aggregations": aggregations,
            "filter": filter,
            "sort": sort,
        },
    }

    response = self._client.post("queryCollection", data).json()

    self.store_recordmap(response["recordMap"])

    return response["result"]

# Fix Bug for notion-py
def search_pages_with_parent(self, parent_id, search=""):
    data = {
        "query": search,
        "parentId": parent_id,
        "limit": 100,
        "spaceId": self.current_space.id,
    }
    response = self.post("searchPagesWithParent", data).json()
    self._store.store_recordmap(response["recordMap"])
    return response["results"]

# 깃허브 리포지토리에 위치한 파일 목록을 가지고 옵니다.
def get_config_file_names(headers, dict_url):
    params = {
        'ref': 'develop'
    }
    
    response = requests.get(dict_url, params=params, headers=headers)

    data = response.json()

    cf_list = []

    for cf in data:
        cf_list.append(cf["name"])
    
    return cf_list

# 파일 목록에서 해당 파일의 컨텐츠를 가지고 옵니다.
def get_file(headers, raw_url, file_names):
    files = []
    for name in file_names:
        url = raw_url + name
        response = requests.get(url, headers=headers)
        files.append({'name':name,'code':response.text})
    return files
    

# json 파일 읽어오기
def js_r(filename: str):
    with open(filename) as f_in:
        return json.load(f_in)




# main func
def main():
    # for notion-py bug
    notion.store.RecordStore.call_load_page_chunk = call_load_page_chunk
    notion.store.RecordStore.call_query_collection = call_query_collection
    notion.client.NotionClient.search_pages_with_parent = search_pages_with_parent

    script_dir = os.path.dirname(__file__) #<-- absolute dir the script is in
    rel_path = "env-py.json"
    abs_file_path = os.path.join(script_dir, rel_path)
    env_data = js_r(abs_file_path)


    print('---')

    MY_GITHUB_TOKEN=env_data['github_token']
    MY_NOTION_TOKEN=env_data['notion_token']
    NOTION_PAGE_URL=env_data['page_url']
    

    headers = {'Content-Type': 'application/vnd.github.v3+json; charset=utf-8','Authorization': 'token ' + MY_GITHUB_TOKEN}

    cf_list = get_config_file_names(headers, env_data['dict_url'])
    cf_files = get_file(headers, env_data['file_url'], cf_list)
    
    
    client = NotionClient(token_v2=MY_NOTION_TOKEN)

    page = client.get_block(NOTION_PAGE_URL)

    
    print("page title", page.title)

    # clear page
    for child in page.children:
        child.remove()


    # create code blocks
    call_block = page.children.add_new(CalloutBlock)
    call_block.title = "이 문서는 RM-Web develop branch 기준으로 자동으로 갱신되는 문서입니다."

    for cf_file in cf_files:
        hb = page.children.add_new(HeaderBlock)
        hb.title = cf_file['name']
        print(cf_file['name'])

        cb = page._children.add_new(CodeBlock)
        cb.title = cf_file['code']
        cb.language = 'javascript'


    print('script finished')

if __name__ == '__main__':
    main()