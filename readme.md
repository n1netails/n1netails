# N1netails

<div align="center">
  <img src="n1netails_icon_transparent.png" alt="N1ne Tails" width="500" style="display: block; margin: auto;"/>
</div>

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](LICENSE)

# Alert Monitoring and Management
N1netails is an open-source project that provides practical alerts and monitoring for applications. If you're tired of relying on complex SIEM tools 
to identify issues — or if your application lacks any alerting at all — N1ne Tails offers a straightforward way to gain 
clarity on problems affecting your applications.

## Requirements
- Java 17
- Maven
- Postgres
- Node.js

# Docker
Run N1netails with docker
### Useful Docker Commands

Build and run the docker container 

Bash (Unix)
```bash
mvn clean package && docker compose up --build
```
PowerShell (Windows)
```shell
mvn clean package; docker-compose up --build
```
Remove docker containers
```bash
docker-compose down -v 
```

# Local Setup
Set up N1ne Tails on your local computer
### Build Locally
To build the project `cd` to the base of the project and run 
```bash
mvn clean package
```
### Develop Locally
Set up N1ne Tails locally by using these readmes:
1. [N1ne Tails Liquibase README](n1netails-liquibase/readme.md)
2. [N1ne Tails Api README](n1netails-api/readme.md)
3. [N1ne Tails Ui README](n1netails-ui/readme.md)

## License
This project is licensed under the **GNU Affero General Public License v3 (AGPLv3)**. See the [LICENSE](./LICENSE) file for details.

### Dual Licensing

N1ne Tails is available under a dual license:

- **AGPLv3** (open source): For community use and contributors. Use freely under open-source terms.
- **Commercial License**: For companies who want to:
    - Keep their changes proprietary
    - Get access to enterprise-grade support, features, or SLAs

To purchase a commercial license or enterprise support, [contact us](mailto:shahidfoy@gmail.com).
See the [LICENSE-commercial](./LICENSE-commercial.txt) file for details.

## Support

For community users, open an issue on GitHub or Join our Discord

[![Join our Discord](https://img.shields.io/badge/Join_Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/ma9CCw7F2x)


[//]: # (For enterprise support, visit [yourcompany.com]&#40;https://yourcompany.com&#41; or email [support@yourdomain.com]&#40;mailto:support@yourdomain.com&#41;.)

## Contributing

Please use the following guidelines for contributions [CONTRIBUTING](./contributing.md)
