CREATE TABLE lottery_videos
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY,
    group_video int NOT NULL,
    index int not null ,
    number int not null,
    duration bigint not null,
    url VARCHAR(500) not null,
    CONSTRAINT lottery_videos_pkey PRIMARY KEY (id)
);

ALTER TABLE public.lottery_session ADD videos varchar(20000) NULL;
ALTER TABLE public.lottery_session ADD duration bigint default 0;