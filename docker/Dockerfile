FROM rabbitmq:3.8.5-management

COPY ./rabbitmq.conf /etc/rabbitmq/rabbitmq.conf

RUN rabbitmq-plugins enable --offline rabbitmq_web_stomp