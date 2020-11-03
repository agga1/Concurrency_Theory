defmodule IOfunc do
  @moduledoc false

  @doc """
  Parsing input
  """
  def parseInput(path) do
    {:ok, contents} = File.read(path)
    [alph_str, word_str | actions_str] = String.split(contents, "\r\n")

    alphabet = alph_str   |> parseAlphabet
    word =    word_str    |> parseWord
    actions = actions_str |> Enum.map(&parseAction/1)

    [alphabet, word , actions]
  end

  def parseAlphabet(line) do
    line
    |> String.split("")
    |> Enum.filter(fn(x) ->
      String.match?(x, ~r/^[[:alnum:]]+$/)
    end)
  end

  def parseWord(wordStr) do
    [name, word] = String.split(wordStr, " = ")
    [name,  word|>String.split("", trim: true)]
  end

  def parseAction(line) do
    [l, r] = String.split(line, "=")
    [name, modified] = String.split(l, " ", trim: true)
    refs = MapSet.new(r
                      |> String.split("", trim: true)
                      |> Enum.filter(fn(x) -> String.match?(x, ~r/^[[:alpha:]]+$/) end) # filter non-letters
    )
    [name, modified, refs]
  end

  @doc """
  formatting output
  """
  def displaySet(set, name) do
    pairs = set |> Enum.map(fn{a, b} -> "(#{a},#{b})" end) |> Enum.join(",")
    IO.puts("#{name} = {#{pairs}}")
  end

  def displayFoata(classes, wordName) do
    ans = classes |> Enum.map(&Enum.join(&1, "")) |> Enum.join(")(")
    IO.puts("FNF[#{wordName}] = (#{ans})")
  end

  @doc """
  formatting output
  """
  def saveAsDotFile(edges, letterIds) do
    edgesStr = edges |> Enum.map(fn {from, to} -> "#{from} -> #{to}" end) |> Enum.join("\n")
    labelsStr = letterIds |> Enum.map(fn {label, id} -> "#{id}[label=#{label}]" end) |> Enum.join("\n")
    str = ["digraph g{", edgesStr, labelsStr, "}"] |> Enum.join("\n")
    File.write("haiku.dot", str)
  end

end
