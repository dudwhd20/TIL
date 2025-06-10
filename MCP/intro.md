# MCP 기초 이론


## 그림으로 보는 MCP

MCP가 요즘 유트브에서도 많이 보일 정도로 핫한 이슈인거 같아서 영상을 시청중 

<img width="738" alt="image" src="https://github.com/user-attachments/assets/e97578ff-6a64-4176-b64a-614f3876f38d"/>

위의 그림과 Claude 데스크톱 애플리케이션을 활용한 MCP 서버를 활용한 예시만 보이고 다른 식의 방식은 없나 찾아보게 되었다

---


위 그림의 Host는 Claude 데스크톱 애플리케이션말고도 MCP Client만 Host안에 구현해 놓으면 사람들이 
올리는 [MCP Repository](https://mcp.so/) 여기에서 MCP Server 를 다운 받아 자신의 LLM의 API key를 넣어 사용하거나
아니면 자신만의 MCP Server를 구축하여 사용하는 방식을 이용 할 수 있다 
 
<img width="761" alt="image" src="https://github.com/user-attachments/assets/ea3ab5a6-6e41-4e6a-b2b6-a46ee57edfc8" />

그림으로 보면 위의 그림이 뭔가 더 이해하기 편리하였다 

---



MCP Client / Server 를 구현하기 편한 SDK는 여러언어로 구현된것은 이미 있지만 java로는 된 SDK는 사용하기엔 client부분은 어려운거 같아
찾아보던 중 Spring Boot AI가 마침 릴리즈 되어 있던 것을 발견하였다 

Spring Boot AI를 활용하여 Host with MCP Client 와 MCP server를 만들어 간단한 쳇봇을 구현 해 보겠다

사용할 LLM은 Open AI를 이용할 예정이다

# 이미지 참고
[MCP Introduction](https://modelcontextprotocol.io/introduction)<br><br>
[LINE 기술블로그](https://techblog.lycorp.co.jp/ko/introduction-to-mcp-and-building-mcp-server-using-line-messaging-api)
