# N1netails

<div align="center">
  <img src="n1netails_icon_transparent.png" alt="N1netails" width="500" style="display: block; margin: auto;"/>
</div>

[![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](LICENSE)

![Stars](https://img.shields.io/github/stars/n1netails/n1netails) 
![Issues](https://img.shields.io/github/issues/n1netails/n1netails)
![Contributors](https://img.shields.io/github/contributors/n1netails/n1netails)
![Last Commit](https://img.shields.io/github/last-commit/n1netails/n1netails)

[//]: # (![GitHub Release]&#40;https://img.shields.io/github/v/release/n1netails/n1netails&#41;)
[//]: # (![Forks]&#40;https://img.shields.io/github/forks/n1netails/n1netails&#41;)
[//]: # (![Docker Pulls]&#40;https://img.shields.io/docker/pulls/n1netails/n1netails?logo=docker&#41;)
[//]: # (![Twitter Follow]&#40;https://img.shields.io/twitter/follow/n1netails?style=social&#41;)

# Alert Monitoring and Management
N1netails is an open-source project that provides practical alerts and monitoring for applications. If you're tired of relying on complex alerting and notification tools 
to identify issues — or if your application lacks any alerting at all — N1netails offers a straightforward way to gain 
clarity on problems affecting your applications.

# Docker
Run N1netails with docker
### Useful Docker Commands

Build and run the docker container

#### docker compose
```shell
docker-compose up --build
```

#### Remove docker containers
```bash
docker-compose down -v 
```

# Local Setup
Set up N1netails on your local computer

### Build all locally
Run the following command at the root of the `n1netails` directory.
```shell
mvn clean package
```

### Build individually
To build the different projects `cd` to the base of the project (Ex. `cd /n1netails-api`) and run 
```bash
mvn clean package
```
### Develop Locally
Set up N1netails locally by using these readmes:
1. [N1netails Liquibase README](n1netails-liquibase/readme.md)
2. [N1netails Api README](n1netails-api/readme.md)
3. [N1netails Ui README](n1netails-ui/readme.md)

## License
This project is licensed under the **GNU Affero General Public License v3 (AGPLv3)**. See the [LICENSE](./LICENSE) file for details.

### Dual Licensing

N1netails is available under a dual license:

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

## N1netails Contributors

Thanks to all the amazing people who contributed to N1netails! 💙

[![Contributors](https://contrib.rocks/image?repo=n1netails/n1netails)](https://github.com/n1netails/n1netails/graphs/contributors)
