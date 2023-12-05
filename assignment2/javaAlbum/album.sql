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