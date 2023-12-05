drop table if exists public.album;
-- Drop table

-- DROP TABLE public.album;

CREATE TABLE public.album (
  id serial primary key,
  image bytea NULL,
  "json" varchar(500) NULL
);

-- Permissions

ALTER TABLE public.album OWNER TO postgres;
GRANT ALL ON TABLE public.album TO postgres;

insert into public.album (id, image, json) values (1, null, '{"artists": "Sex Pistols", "year": "1977", "title": "Sex Pistols"}');

CREATE TABLE public.album_like (
  id serial primary key,
  like_count int4 NULL,
  dislike_count int4 NULL
);

-- Permissions

ALTER TABLE public.album_like OWNER TO postgres;
GRANT ALL ON TABLE public.album_like TO postgres;